import React, { useState } from "react";
import Graph from "./Graph";
import { useStoreContext } from "../../contextApi/ContextApi";
import { useFetchMyShortUrls, useFetchTotalClicks } from "../../hooks/useQuery";
import ShortenPopup from "./ShortenPopup";
import { BiLink } from "react-icons/bi";
import ShortUrlList from "./ShortUrlList";
import { useNavigate } from "react-router-dom";
import Loader from "../Loader";

const DashboardLayout = () => {
  // const refetch = false;
  const navigate = useNavigate();
  const { token } = useStoreContext();
  const [shortenPopup, setShortenPopup] = useState(false);

  //   console.log(useFetchTotalClicks(token, onError));

  const {
    isLoading,
    data: myShortenUrls = [],
    refetch,
  } = useFetchMyShortUrls(token, onError);

  const { isLoading: loader, data: totalClicks } = useFetchTotalClicks(
    token,
    onError,
  );

  function onError() {
    navigate("/error");
  }

  return (
    <div className="lg:px-14 sm:px-8 px-4 min-h-[calc(100vh-64px)]">
      {loader ? (
        <Loader />
      ) : (
        <div className="lg:w-[90%] w-full mx-auto py-16">
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl shadow-2xl p-6 h-96">
            {totalClicks.length === 0 ? (
              <div className="flex flex-col justify-center items-center h-full">
                <h1 className="text-slate-800 font-merriweather sm:text-2xl text-[18px] font-bold mb-1">
                  No links created yet!
                </h1>
                <h3 className="sm:w-96 text-center sm:text-lg text-sm text-slate-600">
                  Create and share your first short URL to start tracking clicks
                  and analytics.
                </h3>
              </div>
            ) : (
              <Graph graphData={totalClicks} />
            )}
          </div>
          <div className="py-5 sm:text-end text-center">
            <button
              className="bg-custom-gradient hover:bg-custom-gradient-hover text-white px-6 py-2.5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300"
              onClick={() => setShortenPopup(true)}
            >
              Create a new short URL
            </button>
          </div>

          <div>
            {!isLoading && myShortenUrls.length === 0 ? (
              <div className="flex justify-center pt-16">
                <div className="flex gap-2 items-center justify-center py-6 sm:px-8 px-5 rounded-md shadow-lg bg-white">
                  <h1 className="text-slate-800 sm:text-[18px] text-[14px] font-semibold mb-1 flex items-center gap-2">
                    <BiLink className="text-blue-500 sm:text-xl text-sm" /> You
                    haven't created any short links yet!
                  </h1>
                </div>
              </div>
            ) : (
              <ShortUrlList data={myShortenUrls} refetch={refetch} />
            )}
          </div>
        </div>
      )}

      <ShortenPopup
        refetch={refetch} // to keep the data updated after creating a new short URL
        open={shortenPopup}
        setOpen={setShortenPopup}
      />
    </div>
  );
};

export default DashboardLayout;
