const mongoose = require('mongoose');

const serviceSchema = new mongoose.Schema(
  {
    legacyId: { type: Number, index: true, unique: true, sparse: true },
    name: { type: String, required: true },
    description: { type: String, required: true },
    price: { type: Number, required: true },
    icon: { type: String, required: true },
    category: { type: String, required: true, index: true }
  },
  { timestamps: true, collection: 'services' }
);

serviceSchema.set('toJSON', {
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

module.exports = mongoose.models.Service || mongoose.model('Service', serviceSchema);
