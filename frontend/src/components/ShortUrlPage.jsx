import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import Loader from "./Loader";

const ShortUrlPage = () => {
  const { url } = useParams();

  useEffect(() => {
    if (url) {
      window.location.href = `${import.meta.env.VITE_BACKEND_URL}/${url}`;
    }
  }, [url]);

  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-64px)] bg-white p-6">
      <Loader />
    </div>
  );
};

export default ShortUrlPage;
