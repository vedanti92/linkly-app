import React from "react";
import { FaFacebook, FaInstagram, FaLinkedin } from "react-icons/fa";
import { FaXTwitter } from "react-icons/fa6";

const Footer = () => {
  return (
    <footer className="bg-custom-gradient text-white py-3 z-40 relative">
      <div className="container mx-auto px-6 lg:px-14 flex flex-col lg:flex-row lg:justify-between items-center gap-2">
        <div className="text-center lg:text-left">
          <h2 className="logo text-3xl mb-1">Linkly</h2>
          <p className="text-xs">Make every click count.</p>
          <p className="text-xs">Shrink it. Share it. Track it.</p>
        </div>

        <p className="mt-2 lg:mt-0 text-sm">
          &copy; 2026 Linkly. All rights reserved.
        </p>

        <div className="flex space-x-4 mt-2 lg:mt-0">
          <a href="#" className="hover:text-gray-200">
            <FaFacebook size={20} />
          </a>
          <a href="#" className="hover:text-gray-200">
            <FaXTwitter size={20} />
          </a>
          <a href="#" className="hover:text-gray-200">
            <FaInstagram size={20} />
          </a>
          <a href="#" className="hover:text-gray-200">
            <FaLinkedin size={20} />
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
