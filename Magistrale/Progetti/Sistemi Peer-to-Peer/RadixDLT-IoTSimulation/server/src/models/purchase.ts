import mongoose from 'mongoose'

export type PurchaseModel = mongoose.Document & {
    aid: string; 
};

const purchaseSchema = new mongoose.Schema({
    aid: String,
});

const Purchase = mongoose.model<PurchaseModel>('purchase', purchaseSchema);

export default Purchase;