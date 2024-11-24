FROM node:16

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

ENV REACT_APP_API_URL=http://localhost:8080

RUN npm install --save-dev @babel/plugin-proposal-private-property-in-object

CMD ["npm", "start"]
