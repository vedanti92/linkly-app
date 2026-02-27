import React, { useState } from "react";
import { useForm } from "react-hook-form";
import TextField from "./TextField";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";
import toast from "react-hot-toast";
import { useStoreContext } from "../contextApi/ContextApi";

const LoginPage = () => {
  const navigate = useNavigate();
  const [loader, setLoader] = useState(false);
  const { setToken } = useStoreContext();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      username: "",
      email: "",
      password: "",
    },
    mode: "onTouched",
  });

  const loginHandler = async (data) => {
    setLoader(true);
    try {
      const { data: response } = await api.post("/api/auth/public/login", data);
      // store the token in local storage
      setToken(response.token);
      localStorage.setItem("JWT_TOKEN", JSON.stringify(response.token));
      toast.success("Logged in successfully!");
      reset();
      navigate("/dashboard");
    } catch (error) {
      toast.error(
        error.response.data?.message ||
          error.response.data ||
          "Logging in failed! Please try again.",
      );
    } finally {
      setLoader(false);
    }
  };

  return (
    <div
      className="min-h-[calc(100vh-64px)] flex justify-center items-center"
      style={{
        backgroundImage: `url("/images/background.jpg")`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <form
        onSubmit={handleSubmit(loginHandler)}
        className="sm:w-[450px] w-[360px] shadow-custom py-8 sm:px-8 px-4 rounded-md bg-white"
      >
        <h1 className="text-center font-merriweather text-btnColor font-bold lg:text-3xl text-2xl">
          Login
        </h1>

        <hr className="mt-2 mb-5 text-black" />

        <div className="flex flex-col gap-3">
          <TextField
            label="Username"
            required
            id="username"
            type="text"
            message="Username is required."
            placeholder="john_doe"
            register={register}
            errors={errors}
          />

          <TextField
            label="Password"
            required
            id="password"
            type="password"
            message="Password is required."
            placeholder="******"
            register={register}
            min={6}
            errors={errors}
          />
        </div>

        <button
          disabled={loader}
          type="submit"
          className="bg-custom-gradient hover:bg-custom-gradient-hover text-white px-6 py-2.5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 w-full my-3"
        >
          {loader ? "Loading..." : "Login"}
        </button>

        <p className="text-center text-sm text-slate-700 mt-6">
          Don't have an account?{" "}
          <Link className="font-semibold" to="/register">
            <span className="text-btnColor underline hover:text-black">
              Register
            </span>
          </Link>
        </p>
      </form>
    </div>
  );
};

export default LoginPage;
