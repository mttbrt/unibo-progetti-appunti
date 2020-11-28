import mongoose from 'mongoose'

export type AccessRequestModel = mongoose.Document & {
    id: string; 
    consumed: boolean;
};
  

const accessRequest = new mongoose.Schema({
    id: String,
    consumed: Boolean,
});

const AccessRequest = mongoose.model<AccessRequestModel>('AccessRequest', accessRequest);

export default AccessRequest;