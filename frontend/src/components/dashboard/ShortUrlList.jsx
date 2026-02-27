import React from "react";
import ShortItem from "./ShortItem";

const ShortUrlList = ({ data, refetch }) => {
  return (
    <div className="my-6 space-y-4">
      {data.map((item) => (
        <ShortItem key={item.id} {...item} refetch={refetch} />
      ))}
    </div>
  );
};

export default ShortUrlList;
