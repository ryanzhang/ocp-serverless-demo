<template>
  <div class="upload-container">
    <h2 class="upload-title">Upload Your Image</h2>
    
    <!-- File upload form -->
    <form @submit.prevent="submitUpload">
      <input 
        type="file" 
        @change="handleFileUpload" 
        class="file-input" 
        accept="image/*" 
        :disabled="isUploading"  
      />
      <button 
        type="submit" 
        class="upload-button" 
        :disabled="isUploading"  
      >
        {{ isUploading ? 'Uploading...' : 'Submit' }}
      </button>
    </form>

    <!-- Show selected file name -->
    <p v-if="file" class="file-info">
      Selected File: {{ file.name }}
    </p>
    
    <!-- Show upload status message (success/failure) -->
    <p v-if="uploadStatus" class="upload-status">{{ uploadStatus }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios';

const file = ref(null);
const uploadStatus = ref('');
const isUploading = ref(false);  // To handle loading state

// Handle file selection
const handleFileUpload = (event) => {
  const selectedFile = event.target.files[0];
  if (selectedFile) {
    file.value = selectedFile;
   uploadStatus.value = '';  // Reset the upload status when a new file is selected

  }
};

// Submit the selected file for upload
const submitUpload = async () => {
  if (!file.value) {
    uploadStatus.value = 'Please select a file to upload.';
    return;
  }

  // Set uploading state and reset status message
  isUploading.value = true;
  uploadStatus.value = '';

  const formData = new FormData();
  formData.append('file', file.value);

  try {
    const response = await axios.post(import.meta.env.VITE_API_URL + '/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    uploadStatus.value = `${response.data}`;
    console.log('Upload response:', response);  // Log full response for debugging
    file.value = null;  // Optionally, reset file after successful upload


  } catch (error) {
    // Detailed error handling
    if (error.response) {
      // Server-side error
      console.error('Server responded with error:', error.response);
      uploadStatus.value = `Server Error: ${error.response.status} - ${error.response.data.message || error.response.data}`;
    } else if (error.request) {
      // No response received from the server
      console.error('No response received:', error.request);
      uploadStatus.value = 'Error: No response from the server. Please check your network or try again later.';
    } else {
      // Other errors (e.g., request setup issues)
      console.error('Error setting up request:', error.message);
      uploadStatus.value = `Error: ${error.message}`;
    }
  } finally {
    // Reset loading state after completion
    isUploading.value = false;
  }
};
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
.file-input {
  margin-top: 20px;
}

.upload-button {
  margin-top: 10px;
}

.upload-status {
  margin-top: 10px;
  font-size: 16px;
  color: green;
}

.upload-status.error {
  color: red;
}
</style>
