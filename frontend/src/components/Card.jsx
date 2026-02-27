import React from "react";
import { motion } from "framer-motion";

const Card = ({ title, desc }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 120 }}
      whileInView={{
        opacity: 1,
        y: 0,
      }}
      viewport={{ once: true }}
      transition={{ duration: 0.8 }}
      className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm hover:shadow-md hover:-translate-y-1 transition-all duration-300 cursor-pointer"
    >
      <h1 className="text-slate-900 text-xl font-bold">{title}</h1>
      <p className="text-slate-700 text-sm">{desc}</p>
    </motion.div>
  );
};

export default Card;
