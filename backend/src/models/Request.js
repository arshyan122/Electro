const mongoose = require('mongoose');

const REQUEST_STATUSES = [
  'pending',
  'accepted',
  'in_progress',
  'completed',
  'rejected',
  'cancelled'
];

const requestSchema = new mongoose.Schema(
  {
    customerId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
      index: true
    },
    technicianId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      default: null,
      index: true
    },
    category: { type: String, required: true, trim: true, index: true },
    title: { type: String, required: true, trim: true },
    description: { type: String, default: '', trim: true, maxlength: 1000 },
    address: { type: String, default: '', trim: true },
    price: { type: Number, default: 0, min: 0 },
    status: {
      type: String,
      enum: REQUEST_STATUSES,
      default: 'pending',
      index: true
    },
    rejectedBy: {
      type: [mongoose.Schema.Types.ObjectId],
      default: [],
      ref: 'User'
    }
  },
  { timestamps: true, collection: 'requests' }
);

requestSchema.set('toJSON', {
  virtuals: false,
  versionKey: false,
  transform: (_doc, ret) => {
    ret.id = ret._id.toString();
    if (ret.customerId) ret.customerId = ret.customerId.toString();
    if (ret.technicianId) ret.technicianId = ret.technicianId.toString();
    if (Array.isArray(ret.rejectedBy)) {
      ret.rejectedBy = ret.rejectedBy.map((x) => x.toString());
    }
    ret.createdAt = ret.createdAt;
    ret.updatedAt = ret.updatedAt;
    delete ret._id;
    return ret;
  }
});

module.exports = {
  Request: mongoose.models.Request || mongoose.model('Request', requestSchema),
  REQUEST_STATUSES
};
