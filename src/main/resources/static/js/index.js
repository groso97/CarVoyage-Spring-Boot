const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const carouselInner = document.querySelector(".carousel-inner");
let index = 0;

function updateCarousel() {
  const totalItems = document.querySelectorAll(".carousel-item").length;
  const newIndex = (index + totalItems) % totalItems;
  carouselInner.style.transform = `translateX(-${newIndex * 100}%)`;
  index = newIndex;
}

prevBtn.addEventListener("click", () => {
  index--;
  updateCarousel();
});

nextBtn.addEventListener("click", () => {
  index++;
  updateCarousel();
});


document.addEventListener("DOMContentLoaded", function () {
    // Postavi trenutni datum i vrijeme
    const dateTimeInput = document.getElementById("date-time");
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");
    const hours = String(now.getHours()).padStart(2, "0");
    const minutes = String(now.getMinutes()).padStart(2, "0");

    const currentDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
    dateTimeInput.value = currentDateTime;

    // Onemogući izbor prošlih datuma i vremena
    dateTimeInput.min = currentDateTime;
  });