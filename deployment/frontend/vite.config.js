import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import nightwatchPlugin from 'vite-plugin-nightwatch'
import { createHtmlPlugin } from 'vite-plugin-html';

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    nightwatchPlugin(),
    createHtmlPlugin({
      inject: {
        injectTo: 'head',
        tags: [
          {
            tag: 'script',
            attrs: {
              src: '/config.js', // Path to your config.js
            },
          },
        ],
      },
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
