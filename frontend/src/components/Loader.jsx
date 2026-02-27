import React from "react";
import { ColorRing } from "react-loader-spinner";

const Loader = () => {
  return (
    <div className="flex justify-center items-center w-full h-[500px]">
      <div className="flex flex-col items-center gap-1">
        <ColorRing
          visible={true}
          height="70"
          width="70"
          ariaLabel="color-ring-loading"
          wrapperStyle={{}}
          wrapperClass="color-ring-wrapper"
          colors={["#1E90FF", "#4169E1", "#5B5FFF", "#6A5ACD", "#8A2BE2"]}
        />
      </div>
    </div>
  );
};

export default Loader;
