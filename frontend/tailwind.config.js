/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        spotify: {
          green: "#1DB954",
          "green-light": "#1ED760",
          black: "#191414",
          "gray-dark": "#282828",
          "gray-light": "#B3B3B3",
          border: "#404040",
        },
      },
    },
  },
  plugins: [],
};
