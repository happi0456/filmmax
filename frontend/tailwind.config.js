/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        paper: '#f4efe4',
        ink: '#1d1b18',
        accent: '#8a2f25',
      },
    },
  },
  plugins: [],
};