import { useQuery } from "@tanstack/react-query";
import api from "../api/api";

export const useFetchMyShortUrls = (token, onError) => {
  return useQuery({
    queryKey: ["my-shortenurls", token],
    enabled: !!token, // only run the query if token is available
    queryFn: async () => {
      const response = await api.get("/api/urls/myurls", {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      return response.data;
    },

    select: (data) => {
      const sortedData = data.sort(
        (a, b) => new Date(b.createdDate) - new Date(a.createdDate),
      );
      return sortedData;
    },

    onError,
    staleTime: 5000,
  });
};

export const useFetchTotalClicks = (token, onError) => {
  return useQuery({
    queryKey: ["url-totalclick", token],
    enabled: !!token, // only run the query if token is available
    queryFn: async () => {
      const response = await api.get(
        "/api/urls/totalClicks?startDate=2026-01-01&endDate=2026-12-31",
        {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      return response.data;
    },

    select: (data) => {
      return Object.keys(data).map((key) => ({
        clickDate: key,
        count: data[key],
      }));
    },

    onError,
    staleTime: 5000,
  });
};
