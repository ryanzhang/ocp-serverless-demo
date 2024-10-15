<template>
  <div>
    <h1>My Image Repo</h1>
    <table>
      <thead>
        <tr>
          <th></th>
          <th></th>
          <th></th>
          <th></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, rowIndex) in imageRows" :key="rowIndex">
          <td v-for="(image, colIndex) in row" :key="colIndex">
            <img :src="image.thumb" alt="Thumbnail" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const imageRows = ref([])

const fetchImages = async () => {
  try {
    const response = await fetch('/fetch_thumbs')
    const data = await response.json()
    
    // Assuming data is an array of image URLs
    const images = data.map(image => ({ thumb: image })) // Adjust according to your API response structure

    // Create rows of 5 images each
    for (let i = 0; i < images.length; i += 5) {
      imageRows.value.push(images.slice(i, i + 5))
    }
  } catch (error) {
    console.error('Error fetching images:', error)
  }
}

onMounted(() => {
  fetchImages()
})
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: center;
}

img {
  max-width: 100px; /* Adjust as necessary */
  max-height: 100px; /* Adjust as necessary */
}
</style>
