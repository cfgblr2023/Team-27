#!/usr/bin/env python
# coding: utf-8

# In[403]:


import pandas as pd
from PIL import Image
import numpy as np
import os
import matplotlib.pyplot as plt
import seaborn as sns
import cv2


# In[404]:


df = pd.read_csv('sensing_data.csv')


# In[405]:


df


# In[406]:


image = Image.open(r'Photos_WalkingAudit/20230323_171615.jpg')
image


# In[408]:


images=[]

filenames=os.listdir("./Photos_WalkingAudit")
# dirp="./Photos_WalkingAudit/"
dirp="Photos_WalkingAudit/"

filenames
for f in filenames:
    path1 = dirp+f
    images.append(path1)
#     images+=load_images_from_folder(dirp+f,True,label=f)
#     if "jpg" in os.listdir(dirp+f)[0]:
# #     df['Photo']=os.listdir(r"./Photos_Walking Audit/{filename}")
#      df['Photo']=os.listdir()


# In[409]:


images


# In[410]:


Image.open(images[2])


# In[411]:


for i in images:
    df['Photo']=i


# In[412]:


df


# In[414]:


indexes = df[df['Category'] == 'Unsafe Zone'].index
print(indexes)


# In[415]:


df.drop(129,inplace=True)
df.drop(133,inplace=True)
df.drop(151,inplace=True)


# In[416]:


from sklearn.utils import shuffle
df=shuffle(df,random_state=0)
df=df.reset_index(drop=True)


# In[417]:


df


# In[418]:


categories_name=sorted(df.Category.unique())
categories_name


# In[419]:


categories_name=sorted(df.Category.unique())
mapper_categories=dict(zip(categories_name,[t for t in range(len(categories_name))]))
df["label" ]=df["Category"].map(mapper_categories)


# In[420]:


df


# In[421]:


cc = df["Category"].value_counts()
plt.figure(figsize=(10,5))
sns.barplot(x = cc.index, y = cc, palette = "crest")
plt.title("Number of pictures of each category", fontsize = 15)
plt.xticks(rotation=90)
plt.show()


# In[422]:


from tqdm import tqdm
# df_new = df.sample(frac = 0.5)
img_paths = df["Photo"].values
img_labels = df["label"].values
X = []
y = []

for i, path in tqdm(enumerate(img_paths), total=len(img_paths), desc="Loading images"):
    img = plt.imread(path)
    img = cv2.resize(img, (64, 64))
    label = img_labels[i]
    X.append(img)
    y.append(label)

X = np.array(X)
y = np.array(y)


# In[423]:


from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size= 0.20, random_state=21)
print(len(X_train))


# In[424]:


# Define the number of classes/categories
num_classes = 4


# In[310]:


# input_shape = (128, 128, 3)  


# In[425]:


input_shape = (64, 64, 3)  
model = Sequential()


# In[426]:


# Add the convolutional layers
model.add(Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=input_shape))
model.add(MaxPooling2D(pool_size=(2, 2)))

model.add(Conv2D(64, kernel_size=(3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))

# Flatten the feature maps
model.add(Flatten())


# In[427]:


# Add the fully connected layers
model.add(Dense(128, activation='relu'))
model.add(Dense(num_classes, activation='softmax'))


# In[428]:


model.compile(optimizer='sgd', loss='categorical_crossentropy', metrics=['accuracy'])


# In[429]:


# Print a summary of the model architecture
model.summary()


# In[430]:


print(to_categorical(y_train))


# In[431]:


callbacks = [EarlyStopping(monitor='val_loss', patience=5),
             ModelCheckpoint(filepath='best_model.h5', monitor='val_loss', save_best_only=True)]
hists = []
y_train = to_categorical(y_train)
model.fit(X_train, y_train, batch_size=32, epochs=40, callbacks=callbacks, validation_split = 0.1, verbose = 1)
hists.append(model.history.history)

