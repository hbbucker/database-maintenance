
import { Linkedin, Github } from "lucide-react";
import React from "react";

const Footer = () => (
  <footer className="border-t py-2 text-center text-xs text-muted-foreground flex justify-center items-center space-x-4 mt-auto">
    <div>by: Hugo B. Bucker</div>
    <a 
      href="https://www.linkedin.com/in/hugo-bastos-bucker/" 
      target="_blank" 
      rel="noopener noreferrer" 
      aria-label="LinkedIn" 
      className="flex items-center gap-1 hover:text-blue-600 underline"
    >
      <Linkedin className="h-4 w-4" />
      LinkedIn
    </a>
    <a 
      href="https://github.com/hbbucker"
      target="_blank"
      rel="noopener noreferrer"
      aria-label="GitHub"
      className="flex items-center gap-1 hover:text-gray-800 underline"
    >
      <Github className="h-4 w-4" />
      GitHub
    </a>
  </footer>
);

export default Footer;
