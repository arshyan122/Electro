const mongoose = require('mongoose');

const ratingSchema = new mongoose.Schema(
  { rate: { type: Number, default: 0 }, count: { type: Number, default: 0 } },
  { _id: false }
);

const productSchema = new mongoose.Schema(
  {
    legacyId: { type: Number, index: true, unique: true, sparse: true },
    title: { type: String, required: true },
    price: { type: Number, required: true },
    description: { type: String, required: true },
    category: { type: String, required: true, index: true },
    image: { type: String, required: true },
    rating: { type: ratingSchema, default: () => ({}) }
  },
  { timestamps: true, collection: 'products' }
);

// Keep the FakeStore-compatible client shape: id (number) instead of _id (ObjectId).
productSchema.set('toJSON', {
  virtuals: false,
  versionKey: false,
  transform: (_doc, ret) => {
    ret.id = ret.legacyId ?? ret._id.toString();
    delete ret._id;
    delete ret.legacyId;
    delete ret.createdAt;
    delete ret.updatedAt;
    return ret;
  }
});

module.exports = mongoose.models.Product || mongoose.model('Product', productSchema);
