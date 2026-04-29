const mongoose = require('mongoose');

const userSchema = new mongoose.Schema(
  {
    email: { type: String, required: true, unique: true, trim: true, lowercase: true },
    name: { type: String, required: true, trim: true },
    passwordHash: { type: String, required: true }
  },
  { timestamps: true, collection: 'users' }
);

userSchema.set('toJSON', {
  virtuals: false,
  versionKey: false,
  transform: (_doc, ret) => {
    ret.id = ret._id.toString();
    delete ret._id;
    delete ret.passwordHash;
    delete ret.createdAt;
    delete ret.updatedAt;
    return ret;
  }
});

module.exports = mongoose.models.User || mongoose.model('User', userSchema);
