const mongoose = require('mongoose');

const technicianSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
      unique: true,
      index: true
    },
    phone: { type: String, default: '', trim: true },
    specialization: { type: [String], default: [] },
    experienceYears: { type: Number, default: 0, min: 0 },
    profileImageUrl: { type: String, default: '' },
    bio: { type: String, default: '', maxlength: 500 },
    available: { type: Boolean, default: true, index: true }
  },
  { timestamps: true, collection: 'technicians' }
);

technicianSchema.set('toJSON', {
  virtuals: false,
  versionKey: false,
  transform: (_doc, ret) => {
    ret.id = ret._id.toString();
    if (ret.userId) ret.userId = ret.userId.toString();
    delete ret._id;
    delete ret.createdAt;
    delete ret.updatedAt;
    return ret;
  }
});

module.exports =
  mongoose.models.Technician || mongoose.model('Technician', technicianSchema);
