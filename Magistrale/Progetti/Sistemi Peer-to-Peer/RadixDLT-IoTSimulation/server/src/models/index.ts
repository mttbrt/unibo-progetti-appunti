import mongoose from 'mongoose';

import AccessRequest from './accessRequest';
import Bus from './bus';
import Purchase from './purchase';

const dbUrl = process.env.DATABASE_URL ? process.env.DATABASE_URL : 'mongodb://localhost:27017/radflix'
const connectDb = () => {
  return mongoose.connect( dbUrl );
};

const models = { AccessRequest, Bus, Purchase };

export { connectDb };

export default models;
