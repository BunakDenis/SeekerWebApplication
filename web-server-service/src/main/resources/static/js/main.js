import '../scss/styles.scss'

//------------------------------------------
//Страница - профайл юзера
const editIcons = document.querySelectorAll('.user-profile-pncl-icon')

if (editIcons) {
  editIcons.forEach(icon => {
    if (icon) {
      const inputField = icon
        .closest('div')
        .querySelector('.profile-user-form-el')
      icon.addEventListener('click', () => {
        editPersonalInfomation(inputField.id)
      })
    }
  })
}
function editPersonalInfomation(inputId) {
  const inputField = document.querySelector(`#${inputId}`)
  inputField.disabled = !inputField.disabled
}

const cancelIcons = document.querySelectorAll('.user-profile-erase-icon')

if (cancelIcons) {
  cancelIcons.forEach(icon => {
    if (icon) {
      const inputField = icon
        .closest('div')
        .querySelector('.profile-user-form-el')
      icon.addEventListener('click', () => {
        cancelEditedProfileInfo(inputField.id)
      })
    }
  })
}
function cancelEditedProfileInfo(inputId) {
  const inputField = document.querySelector(`#${inputId}`)
  inputField.value = ''
}

const profileSeekerAdmEditIcons = document.querySelectorAll(
  '.profile-adm-edit-icon'
)
const profileSeekerModalCancelIcons = document.querySelectorAll(
  '#profile-seeker-modal-cross-svg'
)

if (profileSeekerAdmEditIcons) {
  profileSeekerAdmEditIcons.forEach(icon => {
    icon.addEventListener('click', () => {
      showSeekerDetailInformation()
    })
  })
}

if (profileSeekerModalCancelIcons) {
  profileSeekerModalCancelIcons.forEach(icon => {
    icon.addEventListener('click', () => {
      hideSeekerDetailInformation()
    })
  })
}

function showSeekerDetailInformation() {
  const modal = document.querySelector('.profile-seeker-detail-information')

  modal.style.display = 'flex'
}
function hideSeekerDetailInformation() {
  const modal = document.querySelector('.profile-seeker-detail-information')

  modal.style.display = 'none'
}

//Изменения видимости пароля в профайле юзера
const profileShowPassIcon = document.querySelector('#profile-show-password')
const profileHidePassIcon = document.querySelector('#profile-hide-password')
const profileInputPass = document.querySelector('#profile-user-password')

if (profileInputPass) {
  profileShowPassIcon.addEventListener('click', () => {
    profileInputPass.setAttribute('type', 'password')
    profileHidePassIcon.style.display = 'block'
    profileShowPassIcon.style.display = 'none'
  })
}

if (profileHidePassIcon) {
  profileHidePassIcon.addEventListener('click', () => {
    profileInputPass.setAttribute('type', 'text')
    profileHidePassIcon.style.display = 'none'
    profileShowPassIcon.style.display = 'block'
  })
}
//------------------------------------------
//Страница - регистрации юзера
//Изменения видимости пароля в форме регистрации юзера
const signUpShowPassIcon = document.querySelector('#sign-up-show-password')
const signUpHidePassIcon = document.querySelector('#sign-up-hide-password')
const signUpInputPass = document.querySelector('#sign-up-pass')

if (signUpShowPassIcon) {
  signUpShowPassIcon.addEventListener('click', () => {
    signUpInputPass.setAttribute('type', 'password')
    signUpShowPassIcon.style.display = 'none'
    signUpHidePassIcon.style.display = 'block'
  })
}

if (signUpHidePassIcon) {
  signUpHidePassIcon.addEventListener('click', () => {
    signUpInputPass.setAttribute('type', 'text')
    signUpShowPassIcon.style.display = 'block'
    signUpHidePassIcon.style.display = 'none'
  })
}
//АВТОЗАПОЛНЕНИЕ НАЗВАНИЯ ГОРОДА
const input = document.getElementById('resident-city')
const suggestionsContainer = document.getElementById('suggestions')

const cache = new Map() // Используем Map для лучшей производительности
let debounceTimeout

// Дебаунс для оптимизации количества запросов
function debounce(func, delay) {
  return function (...args) {
    clearTimeout(debounceTimeout)
    debounceTimeout = setTimeout(() => func.apply(this, args), delay)
  }
}

// Определяем язык ввода на основе запроса
function detectLanguage(query) {
  const cyrillicPattern = /[\u0400-\u04FF]/
  const ukrainianPattern = /[\u0400-\u045F]/ // Украинские символы входят в диапазон кириллицы

  if (ukrainianPattern.test(query)) {
    return 'uk'
  }
  return cyrillicPattern.test(query) ? 'ru' : 'en'
}

// Функция запроса к Nominatim API с форматом GeocodeJSON
async function fetchCitySuggestions(query) {
  if (!query || query.length < 3) {
    suggestionsContainer.innerHTML = ''
    return
  }

  const lang = detectLanguage(query) // Определяем язык

  if (cache.has(query)) {
    displaySuggestions(cache.get(query)) // Используем данные из кэша
    return
  }

  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?format=geocodejson&q=${encodeURIComponent(
        query
      )}&addressdetails=1&limit=8&accept-language=${lang}`
    )
    const data = await response.json()
    const features = data.features || []

    cache.set(query, features) // Кэшируем результаты
    displaySuggestions(features)
  } catch (error) {
    console.error('Ошибка при получении данных с Nominatim API:', error)
  }
}

// Функция для отображения подсказок
function displaySuggestions(suggestions) {
  suggestionsContainer.innerHTML = '' // Очистка контейнера

  if (suggestions.length === 0) {
    suggestionsContainer.innerHTML = '<div>Город не найден</div>'
    return
  }

  const fragment = document.createDocumentFragment()

  suggestions.forEach(suggestion => {
    const cityName =
      suggestion.properties?.geocoding?.name || 'Неизвестный город'
    const country =
      suggestion.properties?.geocoding?.country || 'Неизвестная страна'
    const displayName = suggestion.properties?.geocoding?.label || ''

    const suggestionItem = document.createElement('div')
    suggestionItem.textContent = `${cityName}, ${country}`

    // Обработчик клика по подсказке
    suggestionItem.addEventListener('click', () => {
      input.value = displayName
      suggestionsContainer.innerHTML = ''
    })

    fragment.appendChild(suggestionItem)
  })

  suggestionsContainer.appendChild(fragment)
}

if (input) {
  // Обработчик ввода с дебаунсом
  input.addEventListener(
    'input',
    debounce(() => {
      const query = input.value.trim()
      fetchCitySuggestions(query)
    }, 300)
  ) // Задержка 300 мс

  // Закрываем подсказки при клике вне контейнера
  document.addEventListener('click', e => {
    if (!e.target.closest('.autocomplete-container')) {
      suggestionsContainer.innerHTML = ''
    }
  })
}
