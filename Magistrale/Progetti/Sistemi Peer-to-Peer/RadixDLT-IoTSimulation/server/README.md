## Introduction

- This boilerplate uses TypeScript 2.9, Express 4, and Webpack 4
  to print a "hello world" on the port 3001.
- For development, it uses `nodemon` to monitor source file changes.
- For production, it uses `webpack` to bundle source files.

## Installation

- Install MongoDB https://treehouse.github.io/installation-guides/mac/mongo-mac.html
- `npm install -g nodemon ts-node typescript`
- `npm install`
- `npm start` for development; `npm run build` for production

## Rationale

- Minimal: Only include necessary packages and settings to print a "hello world".
- Hot reload: Use `nodemon` instead of `webpack` HMR since `nodemon` is much easier and more flexible.
