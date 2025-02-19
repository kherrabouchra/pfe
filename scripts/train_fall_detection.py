import tensorflow as tf
import numpy as np
from sklearn.model_selection import train_test_split

def create_model():
    model = tf.keras.Sequential([
        tf.keras.layers.Input(shape=(15,)),  # 15 features
        tf.keras.layers.Dense(64, activation='relu'),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Dense(32, activation='relu'),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Dense(1, activation='sigmoid')
    ])
    
    model.compile(
        optimizer='adam',
        loss='binary_crossentropy',
        metrics=['accuracy']
    )
    return model

# Features we extract:
# 1. Signal Vector Magnitude (SVM)
# 2. Signal Energy
# 3. Jerk
# 4. Rotational Energy
# 5. Orientation Changes
# ... etc.

# Example training data structure
X = np.array([
    # [SVM, Energy, Jerk, RotEnergy, OrientChange, ...]
    [1.2, 0.5, 0.3, 0.8, 0.2, ...],  # Fall example
    [0.3, 0.2, 0.1, 0.2, 0.1, ...],  # Non-fall example
    # ... more examples
])

y = np.array([1, 0, ...])  # 1 for fall, 0 for non-fall

# Split data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

# Create and train model
model = create_model()
model.fit(X_train, y_train, epochs=50, validation_data=(X_test, y_test))

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save model
with open('fall_detection_model.tflite', 'wb') as f:
    f.write(tflite_model) 