import React from "react";
import { BiLink } from "react-icons/bi";
import { GrSecure } from "react-icons/gr";
import { TbHandClick } from "react-icons/tb";
import { VscGraph } from "react-icons/vsc";
import { motion } from "framer-motion";

const features = [
  {
    icon: <BiLink className="text-indigo-500 text-3xl" />,
    title: "Simple & Smart Shortening",
    desc: "Create short, memorable links in seconds. No complexity, no friction — just paste, generate, and share. Linkly makes link management effortless from the very first click.",
  },
  {
    icon: <VscGraph className="text-purple-500 text-3xl" />,
    title: "Insightful Analytics",
    desc: "Go beyond shortening. Track clicks, monitor performance, and understand how your links are being used. With clear, real-time insights, you can make smarter decisions backed by data.",
  },
  {
    icon: <GrSecure className="text-green-500 text-3xl" />,
    title: "Built for Security",
    desc: "Your links deserve protection. Linkly uses secure infrastructure and reliable systems to ensure your data stays safe and your redirects remain trustworthy.",
  },
  {
    icon: <TbHandClick className="text-amber-400 text-3xl" />,
    title: "Reliable at Every Click",
    desc: "Every click matters. That’s why Linkly delivers lightning-fast redirects and dependable uptime — so your audience gets a seamless experience every time.",
  },
];

const AboutPage = () => {
  // Animation variants for feature cards
  const cardVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } },
    hover: { scale: 1.05, transition: { duration: 0.3 } },
  };

  return (
    <div className="relative min-h-[calc(100vh-64px)] lg:px-14 sm:px-8 px-5 pt-12 bg-gradient-to-b from-slate-50 via-white to-white">
      {/* subtle radial glow */}
      <div className="absolute inset-0 -z-10">
        <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-indigo-100/20 blur-3xl rounded-full"></div>
        <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-purple-100/20 blur-3xl rounded-full"></div>
      </div>

      <div className="max-w-6xl mx-auto">
        {/* Page title */}
        <motion.h1
          className="sm:text-5xl text-4xl font-bold text-slate-900 mb-4"
          initial={{ opacity: 0, y: -30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          About Linkly
        </motion.h1>

        {/* Page description */}
        <motion.p
          className="text-slate-600 text-lg mb-12 max-w-3xl"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.6, delay: 0.2 }}
        >
          Linkly helps you transform long, cluttered URLs into clean, powerful
          links you can confidently share anywhere. Designed for simplicity and
          built for performance, Linkly gives you full control over every link
          you create.
        </motion.p>

        {/* Feature cards */}
        <div className="grid sm:grid-cols-2 gap-8">
          {features.map((feature, idx) => (
            <motion.div
              key={idx}
              className="bg-white border border-slate-200 rounded-2xl p-6 flex space-x-4 shadow-sm cursor-pointer"
              variants={cardVariants}
              initial="hidden"
              animate="visible"
              whileHover="hover"
              transition={{ delay: idx * 0.1 }}
            >
              <div className="flex-shrink-0 mt-1">{feature.icon}</div>
              <div>
                <h2 className="text-2xl font-bold text-slate-900 mb-2">
                  {feature.title}
                </h2>
                <p className="text-slate-500 text-sm">{feature.desc}</p>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AboutPage;
