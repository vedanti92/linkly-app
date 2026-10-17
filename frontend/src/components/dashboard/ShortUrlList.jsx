import React from "react";
import ShortItem from "./ShortItem";

const ShortUrlList = ({ data, refetch, page, onPageChange }) => {
  const urls = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;
  const isFirst = data?.first ?? true;
  const isLast = data?.last ?? true;

  return (
    <div className="my-6">
      {urls.length > 0 ? (
        <div className="space-y-4">
          {urls.map((item) => (
            <ShortItem key={item.id} {...item} refetch={refetch} />
          ))}
        </div>
      ) : (
        <p className="py-6 text-center text-gray-500">
          No shortened URLs found.
        </p>
      )}

      {totalPages > 0 && (
        <div className="mt-6 flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm text-gray-500">
            Page {page + 1} of {totalPages}
            {" · "}
            {totalElements} URLs
          </p>

          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => onPageChange(page - 1)}
              disabled={isFirst}
              className="rounded-md border px-4 py-2 transition-all duration-200 hover:bg-blue-500 hover:text-white hover:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:bg-transparent disabled:hover:text-current disabled:hover:border-gray-300"
            >
              Previous
            </button>

            <button
              type="button"
              onClick={() => onPageChange(page + 1)}
              disabled={isLast}
              className="rounded-md border px-4 py-2 transition-all duration-200 hover:bg-blue-500 hover:text-white hover:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:bg-transparent disabled:hover:text-current disabled:hover:border-gray-300"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ShortUrlList;
