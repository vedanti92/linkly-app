import dayjs from "dayjs";
import React, { useEffect, useState } from "react";
import { FaExternalLinkAlt, FaRegCalendarAlt } from "react-icons/fa";
import { BsCheckAll } from "react-icons/bs";
import {
  MdAnalytics,
  MdContentCopy,
  MdDelete,
  MdEdit,
  MdOutlineAdsClick,
} from "react-icons/md";
import { useNavigate } from "react-router-dom";
import { useStoreContext } from "../../contextApi/ContextApi";
import api from "../../api/api";
import { ColorRing } from "react-loader-spinner";
import Graph from "./Graph";
import { deleteUrl, updateUrl } from "../../api/urlService";
import toast from "react-hot-toast";
import { motion } from "framer-motion";

const ShortItem = ({
  id,
  originalUrl,
  shortUrl,
  clickCount,
  createdDate,
  refetch,
}) => {
  const { token } = useStoreContext();
  const navigate = useNavigate();
  const [copied, setCopied] = useState(false);
  const [analyticToggle, setAnalyticToggle] = useState(false);
  const [selectedUrl, setSelectedUrl] = useState("");
  const [loader, setLoader] = useState(false);
  const [analyticsData, setAnalyticsData] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [newOriginalUrl, setNewOriginalUrl] = useState(originalUrl);
  const [actionLoading, setActionLoading] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(
        `${import.meta.env.VITE_REACT_SUBDOMAIN}/${shortUrl}`,
      );
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error("Copy failed", err);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("Are you sure you want to delete this URL?"))
      return;

    try {
      setActionLoading(true);
      await deleteUrl(id, token);
      toast.success("URL deleted successfully!");
      refetch();
    } catch (error) {
      toast.error("Failed to delete URL!");
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdate = async () => {
    if (!newOriginalUrl.trim()) {
      toast.error("URL cannot be empty!");
      return;
    }

    try {
      setActionLoading(true);

      await updateUrl(id, newOriginalUrl, token);

      toast.success("URL updated successfully!");
      setIsEditing(false);
      refetch();
    } catch (err) {
      toast.error("Failed to update URL!");
    } finally {
      setActionLoading(false);
    }
  };

  const subDomain = import.meta.env.VITE_REACT_SUBDOMAIN.replace(
    /^https?:\/\//,
    "",
  );

  const analyticsHandler = (shortUrl) => {
    if (!analyticToggle) {
      setSelectedUrl(shortUrl);
    }
    setAnalyticToggle(!analyticToggle);
  };

  const fetchMyShortUrl = async () => {
    setLoader(true);
    try {
      const { data } = await api.get(
        `/api/urls/analytics/${selectedUrl}?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59`,
        {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      setAnalyticsData(data);
      setSelectedUrl("");
    } catch (error) {
      navigate("/error");
      console.log(error);
    } finally {
      setLoader(false);
    }
  };

  useEffect(() => {
    if (selectedUrl) {
      fetchMyShortUrl();
    }
  }, [selectedUrl]);

  return (
    <div className="relative bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-lg hover:-translate-y-1 transition-transform duration-300">
      <div className="flex sm:flex-row flex-col sm:justify-between gap-5 py-5">
        {/* LEFT SECTION */}
        <div className="flex-1 space-y-2">
          {/* Short URL */}
          <a
            href={`${import.meta.env.VITE_REACT_SUBDOMAIN}/${shortUrl}`}
            target="_blank"
            rel="noreferrer"
            className="text-[17px] font-semibold text-linkColor flex items-center gap-2"
          >
            {subDomain}/{shortUrl}
            <FaExternalLinkAlt className="text-linkColor" />
          </a>

          {/* EDIT FIELD */}
          {isEditing && (
            <div className="gap-2 mt-2 mr-30">
              <input
                type="text"
                value={newOriginalUrl}
                onChange={(e) => setNewOriginalUrl(e.target.value)}
                className="border px-2 py-1 rounded-md w-full"
              />
              <div className="flex items-center gap-2 mt-2">
                <button
                  onClick={handleUpdate}
                  className="bg-green-500 text-white px-3 py-1 rounded-md text-sm border hover:border-black hover:text-black hover:bg-white transition-all duration-150"
                >
                  Save
                </button>
                <button
                  onClick={() => setIsEditing(false)}
                  className="bg-gray-400 text-white px-3 py-1 rounded-md text-sm border hover:border-black hover:text-black hover:bg-white transition-all duration-150"
                >
                  Cancel
                </button>
              </div>
            </div>
          )}

          {/* Original URL */}
          <h3 className="text-slate-700">{originalUrl}</h3>

          {/* Stats */}
          <div className="flex items-center gap-8 pt-4">
            <div className="flex items-center gap-1 font-semibold text-green-600">
              <MdOutlineAdsClick />
              {clickCount} {clickCount === 1 ? "Click" : "Clicks"}
            </div>

            <div className="flex items-center gap-1 font-semibold text-slate-800">
              <FaRegCalendarAlt />
              {dayjs(createdDate).format("DD MMM YYYY")}
            </div>
          </div>
        </div>

        {/* RIGHT BUTTONS */}
        <div className="flex items-center gap-3">
          <button
            onClick={handleCopy}
            className="bg-blue-500 text-white px-3 py-1 rounded-md text-sm border hover:border-black hover:text-black hover:bg-white transition-all duration-150"
          >
            {copied ? (
              <span className="flex items-center gap-1">
                <BsCheckAll /> Copied
              </span>
            ) : (
              <span className="flex items-center gap-1">
                <MdContentCopy /> Copy
              </span>
            )}
          </button>

          <button
            onClick={() => analyticsHandler(shortUrl)}
            className="bg-violet-500 text-white px-3 py-1 rounded-md text-sm border hover:border-black hover:text-black hover:bg-white transition-all duration-150"
          >
            <span className="flex items-center gap-1">
              <MdAnalytics /> Analytics
            </span>
          </button>

          <button
            onClick={() => setIsEditing(!isEditing)}
            className="bg-amber-500 text-white px-3 py-1 rounded-md text-sm border hover:border-black hover:text-black hover:bg-white transition-all duration-150"
          >
            <span className="flex items-center gap-1">
              <MdEdit /> Edit
            </span>
          </button>

          <button
            onClick={handleDelete}
            disabled={actionLoading}
            className="bg-red-500 text-white px-3 py-1 rounded-md text-sm border hover:border-black hover:text-black hover:bg-white transition-all duration-150"
          >
            <span className="flex items-center gap-1">
              <MdDelete /> Delete
            </span>
          </button>
        </div>
      </div>

      {/* ANALYTICS */}
      {analyticToggle && (
        <div className="mt-6 border-t pt-4 min-h-[300px]">
          <motion.div
            key="analytics"
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.4, ease: "easeInOut" }}
            className="min-h-[300px]"
          >
            {loader ? (
              <div className="flex justify-center items-center h-60">
                <ColorRing
                  visible={true}
                  height="50"
                  width="50"
                  colors={[
                    "#1E90FF",
                    "#4169E1",
                    "#5B5FFF",
                    "#6A5ACD",
                    "#8A2BE2",
                  ]}
                />
              </div>
            ) : analyticsData.length === 0 ? (
              <div className="text-center text-slate-600 font-semibold">
                No activity yet for this link.
              </div>
            ) : (
              <Graph graphData={analyticsData} />
            )}
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default ShortItem;
