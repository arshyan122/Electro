const mongoose = require('mongoose');

const technicianServiceSchema = new mongoose.Schema(
  {
    technicianId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
      index: true
    },
    name: { type: String, required: true, trim: true },
    description: { type: String, default: '', trim: true, maxlength: 500 },
    category: { type: String, required: true, trim: true, index: true },
    price: { type: Number, required: true, min: 0 },
    active: { type: Boolean, default: true }
  },
  { timestamps: true, collection: 'technicianServices' }
);

technicianServiceSchema.set('toJSON', {
  virtuals: false,
  versionKey: false,
  transform: (_doc, ret) => {
    ret.id = ret._id.toString();
    if (ret.technicianId) ret.technicianId = ret.technicianId.toString();
    delete ret._id;
    delete ret.createdAt;
    delete ret.updatedAt;
    return ret;
  }
});

module.exports =
  mongoose.models.TechnicianService ||
  mongoose.model('TechnicianService', technicianServiceSchema);
