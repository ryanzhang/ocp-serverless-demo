<template>
  <div class="upload-container">
    <h2 class="upload-title">Upload Your Image</h2>
    <input type="file" @change="handleFileUpload" class="file-input" />
    <button @click="submitUpload" class="upload-button">Submit</button>
    <p v-if="file" class="file-info">{{ file.name }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const file = ref(null)

const handleFileUpload = (event) => {
  file.value = event.target.files[0]
}

const submitUpload = () => {
  if (file.value) {
    // Handle the file upload logic here
    console.log('Uploading:', file.value)
    // You can make an API call or any other action with the file
  } else {
    console.error('No file selected for upload.')
  }
}
</script>

<style scoped>
.upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 2px dashed #007bff;
  border-radius: 10px;
  background-color: #f8f9fa;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s;
}

.upload-container:hover {
  background-color: #e9ecef;
}

.upload-title {
  margin-bottom: 15px;
  font-size: 1.5rem;
  color: #343a40;
}

.file-input {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 5px;
  font-size: 1rem;
  width: 100%;
  max-width: 300px;
}

.upload-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.upload-button:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.file-info {
  margin-top: 10px;
  color: #6c757d;
  font-size: 0.9rem;
}
</style>
