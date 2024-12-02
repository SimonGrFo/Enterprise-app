FROM node:16
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm install --save-dev @babel/plugin-proposal-private-property-in-object
CMD ["npm", "start"]