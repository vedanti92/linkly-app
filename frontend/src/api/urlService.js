import api from "./api";

export const deleteUrl = async (shortUrl, token) => {
  try {
    const response = await api.delete(`/api/urls/${shortUrl}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Deletion failed!";
  }
};

export const updateUrl = async (id, originalUrl, token) => {
  try {
    const response = await api.put(
      `/api/urls/${id}`,
      { originalUrl },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );

    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Updation failed!";
  }
};
