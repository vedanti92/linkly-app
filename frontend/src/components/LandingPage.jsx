import React from "react";
import Card from "./Card";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useStoreContext } from "../contextApi/ContextApi";

const LandingPage = () => {
  const navigate = useNavigate();
  const { token } = useStoreContext();

  const dashboardNavigateHandler = () => {
    navigate("/dashboard");
  };
  return (
    <div className="relative overflow-hidden min-h-[calc(100vh-64px)] bg-white lg:px-14 sm:px-8 px-4">
      <div className="lg:flex-row flex-col lg:py-5 pt-16 lg:gap-10 gap-8 flex justify-between items-center">
        <div className="flex-1">
          <motion.h1
            initial={{ opacity: 0, y: -80 }}
            whileInView={{
              opacity: 1,
              y: 0,
            }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="font-bold text-slate-800 md:text-5xl sm:text-4xl text-3xl md:leading-[55px] sm:leading-[45px] leading-10 lg:w-full md:w-[70%] w-full"
          >
            Short links. Smarter sharing.
          </motion.h1>
          <p className="text-slate-600 text-sm my-5 font-bold">
            Linkly turns long, messy URLs into clean, powerful links in seconds.
            Create, manage, and track every link with ease — all from one simple
            dashboard.
          </p>
          <p className="text-slate-500 text-sm my-5">
            Whether you're sharing content, running campaigns, or collaborating
            with a team, Linkly gives you the tools to stay organized and in
            control. Monitor performance in real time, understand where your
            clicks are coming from, and optimize every share with confidence.
          </p>
          <p className="text-slate-500 text-sm my-5">
            Designed for speed and built for reliability, Linkly ensures every
            redirect is fast, secure, and seamless. No clutter. No complexity.
            Just smarter links that work for you.
          </p>

          <div className="flex items-center gap-3">
            <motion.button
              initial={{ opacity: 0, y: 80 }}
              whileInView={{
                opacity: 1,
                y: 0,
              }}
              viewport={{ once: true }}
              transition={{ duration: 0.8 }}
              onClick={dashboardNavigateHandler}
              className="bg-custom-gradient hover:bg-custom-gradient-hover text-white px-6 py-2.5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300"
            >
              Manage Links
            </motion.button>
            <motion.button
              initial={{ opacity: 0, y: 80 }}
              whileInView={{
                opacity: 1,
                y: 0,
              }}
              viewport={{ once: true }}
              transition={{ duration: 0.8 }}
              onClick={dashboardNavigateHandler}
              className="border border-slate-300 text-slate-700 px-6 py-2.5 rounded-2xl hover:border-slate-400 transition-all duration-300"
            >
              Create Short Link
            </motion.button>
          </div>
        </div>

        <div className="flex-1 flex justify-center w-full">
          <motion.img
            initial={{ opacity: 0 }}
            whileInView={{
              opacity: 1,
            }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="sm:w-[480px] w-[400px] object-cover rounded-md"
            src="/images/logo.png"
            alt="Image"
          />
        </div>
      </div>
      <div className="sm:pt-12">
        <motion.p
          initial={{ opacity: 0, y: 50 }}
          whileInView={{
            opacity: 1,
            y: 0,
          }}
          viewport={{ once: true }}
          transition={{ duration: 0.8 }}
          className="text-slate-800 font-bold lg:w-[60%] md:w-[70%] sm:w-[80%] mx-auto text-3xl text-center mb-5"
        >
          Built for creators, teams, and modern businesses.
        </motion.p>

        <div className="pt-4 pb-7 grid lg:gap-7 gap-4 xl:grid-cols-4 lg:grid-cols-3 sm:grid-cols-2 grid-cols-1 mt-5">
          <Card
            title="Simple & Smart Shortening"
            desc="Create short, memorable links in seconds. No complexity, no
                friction — just paste, generate, and share. Linkly makes link
                management effortless from the very first click."
          />
          <Card
            title="Insightful Analytics"
            desc="Go beyond shortening. Track clicks, monitor performance, and
                understand how your links are being used. With clear, real-time
                insights, you can make smarter decisions backed by data."
          />
          <Card
            title="Built for Security"
            desc="Your links deserve protection. Linkly uses secure infrastructure
                and reliable systems to ensure your data stays safe and your
                redirects remain trustworthy."
          />
          <Card
            title="Reliable at Every Click"
            desc="Every click matters. That’s why Linkly delivers lightning-fast
                redirects and dependable uptime — so your audience gets a
                seamless experience every time."
          />
        </div>
      </div>
    </div>
  );
};

export default LandingPage;
