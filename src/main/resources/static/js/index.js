const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const carouselInner = document.querySelector(".carousel-inner");
let index = 0;
const itemsPerPage = 3;

function updateCarousel() {
  const totalItems = document.querySelectorAll(".carousel-item").length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  
  if (index <= 0) {
    index = 0;
    prevBtn.disabled = true;
  } else {
    prevBtn.disabled = false;
  }
  
  if (index >= totalPages - 1) {
    index = totalPages - 1;
    nextBtn.disabled = true;
  } else {
    nextBtn.disabled = false;
  }

  carouselInner.style.transform = `translateX(-${index * 100}%)`;
}

prevBtn.addEventListener("click", () => {
  if (index > 0) {
    index--;
    updateCarousel();
  }
});

nextBtn.addEventListener("click", () => {
  const totalItems = document.querySelectorAll(".carousel-item").length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  if (index < totalPages - 1) {
    index++;
    updateCarousel();
  }
});

updateCarousel(); // Initial call to set the correct state of the buttons



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



  function showCategory(category) {
    const categories = document.querySelectorAll('.faq-category');
    categories.forEach(cat => cat.classList.add('hidden'));

    document.getElementById(category).classList.remove('hidden');

    const buttons = document.querySelectorAll('button[id$="-btn"]');
    buttons.forEach(btn => btn.classList.remove('bg-[#1ECB15]', 'text-white'));
    buttons.forEach(btn => btn.classList.add('bg-gray-100', 'text-gray-700'));

    document.getElementById(category + '-btn').classList.remove('bg-gray-100', 'text-gray-700');
    document.getElementById(category + '-btn').classList.add('bg-[#1ECB15]', 'text-white');
}

function toggleAnswer(index) {
    const answers = document.querySelectorAll('.faq-answer');
    
    // Hide all answers
    answers.forEach(answer => answer.classList.add('hidden'));

    // Show the selected answer
    answers[index].classList.remove('hidden');
}

// Default view
showCategory('reservation');
