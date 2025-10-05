//Функция иморта хедера
fetch('./partials/header.txt')
  .then(response => response.text())
  .then(data => {
    document.getElementById('page-header').innerHTML = data
    initHeaderJS() // Инициализация JavaScript из модуля
  })
  .catch(error => console.error('Ошибка загрузки header:', error))

//Функция иморта футера
fetch('./partials/footer.txt')
  .then(response => response.text())
  .then(data => {
    document.getElementById('page-footer').innerHTML = data
    initFooterJS() // Инициализация JavaScript из модуля
  })
  .catch(error => console.error('Ошибка загрузки footer:', error))

/**
 * Загружает текстовый файл и возвращает DOM-элемент
 * @param {string} path - Путь к текстовому файлу
 * @returns {Promise<Element>} - Объект DOM, созданный из содержимого файла
 */
export async function loadTextFileAsElement(path) {
  try {
    const response = await fetch(path)
    if (!response.ok) {
      throw new Error(`Ошибка загрузки файла: ${response.statusText}`)
    }

    const text = await response.text()
    const tempContainer = document.createElement('div')
    tempContainer.innerHTML = text.trim()
    return tempContainer.firstElementChild // Возвращаем первый вложенный элемент
  } catch (error) {
    console.error('Ошибка при получении элемента из файла:', error)
    return null
  }
}

//Хедер
function initHeaderJS() {
  const userEmail = 'True_Finder@mystic-school.ru'
  const userPassword = 'incompleteness2025'

  // Изменение цвета логотипа Єгрегора и названия Школы при наведении или на логотип или на текст
  const alVadudIcon = document.querySelector('.al-vadud')
  const shcoolName = document.querySelector('.school-name')
  alVadudIcon.addEventListener('mouseenter', function () {
    changeLogoColor(true)
  })

  alVadudIcon.addEventListener('mouseleave', function () {
    changeLogoColor(false)
  })

  shcoolName.addEventListener('mouseenter', function () {
    changeLogoColor(true)
  })

  shcoolName.addEventListener('mouseleave', function () {
    changeLogoColor(false)
  })

  function changeLogoColor(isFocuse) {
    const alVadudIcon = document.querySelector('.al-vadud')
    const shcoolName = document.querySelector('.school-name')
    if (isFocuse) {
      shcoolName.style.color = 'gold'
      alVadudIcon.style.fill = 'gold'
    } else {
      shcoolName.style.color = 'red'
      alVadudIcon.style.fill = 'red'
    }
  }

  //Контейнер поиска по сайту
  const searchToggle = document.querySelector('.searchToggle')

  searchToggle.addEventListener('click', () => {
    searchToggle.classList.toggle('active')
  })

  //------------------------------------------

  // Переключатель меню входа юзера и меню уведомлений
  // Переменная для определения авторизации пользователя
  let userAuthorization
  const userAuthorizationKey = 'userAuthorization'

  if (localStorage.getItem(userAuthorizationKey)) {
    userAuthorization = localStorage.getItem('userAuthorization')
  } else {
    userAuthorization = false
  }

  // Элементы HTML
  const userIcon = document.getElementById('user') // SVG с классом user
  const signInContainer = document.querySelector('.sign-in-container')
  const userInformationContainer = document.getElementById(
    'user-information-container'
  )
  const signInCancelButton = document.querySelector('.sign-in-form-cancel')
  const userInformationCancelButton = document.querySelector(
    '.user-information-form-cancel'
  )
  // Функция для обработки нажатия на SVG
  if (userIcon) {
    userIcon.addEventListener('click', () => {
      if (userAuthorization) {
        // Если пользователь авторизован, показать контейнер уведомлений
        if (userInformationContainer.style.display == 'block') {
          userInformationContainer.style.display = 'none'
        } else {
          signInContainer.style.display = 'none'
          userInformationContainer.style.display = 'block'
        }
      } else {
        // Если пользователь не авторизован, показать контейнер входа
        if (signInContainer.style.display == 'block') {
          signInContainer.style.display = 'none'
        } else {
          signInContainer.style.display = 'block'
          userInformationContainer.style.display = 'none'
        }
      }
    })
  }
  //Функция скрытия формы при нажатии на крестик
  if (signInCancelButton) {
    signInCancelButton.addEventListener('click', () => {
      signInContainer.style.display = 'none'
    })
  }

  if (userInformationCancelButton) {
    userInformationCancelButton.addEventListener('click', () => {
      userInformationContainer.style.display = 'none'
    })
  }
  /*
        Переключение форм входа юзера и информации о юзере. При нажатии на кнопку "выйти" и последующем обновлении страницы в информации о юзере будет 
        отображаться форма регистрации юзера.
        При нажатии на кнопку "войти" в форме регистрации юзера, при следующем обновлении страницы будет отображаться форма информации о юзере
    */
  const signOutLink = document.querySelector('.sign-out-link')
  const signInButton = document.querySelector('.sign-in-button')

  /* istanbul ignore next */
  signOutLink.addEventListener('click', () => {
    userAuthorization = false
  })

  /* istanbul ignore next */
  signInButton.addEventListener('click', () => {
    const inputUserEmail = document.querySelector(
      '#sign-in-user-email-input'
    ).value
    const inputPassword = document.querySelector(
      '#sign-in-password-input'
    ).value

    if (inputUserEmail === userEmail && inputPassword === userPassword) {
      localStorage.setItem(userAuthorizationKey, 'true')
    } else {
      alert('Неверный логин или пароль')
    }
  })

  //------------------------------------------
  // Переключатель меню уведомлений

  // Элементы HTML
  const notificationIcon = document.querySelector('.notification-svg') // SVG с классом user
  const notificationContainer = document.querySelector(
    '.notification-container'
  )
  const notificationCancelButton = document.querySelector(
    '.notification-form-cancel'
  )

  // Функция для обработки нажатия на SVG
  notificationIcon.addEventListener('click', () => {
    if (notificationContainer.style.display == 'block') {
      notificationContainer.style.display = 'none'
    } else {
      notificationContainer.style.display = 'block'
    }
  })

  notificationCancelButton.addEventListener('click', () => {
    notificationContainer.style.display = 'none'
  })

  //------------------------------------------
  //Изменения видимости пароля в форме входа юзера
  const showPasswordIcon = document.querySelector('#show-password')
  const hidePasswordIcon = document.querySelector('#hide-password')
  const passwordInputField = document.querySelector('#sign-in-password-input')

  if (showPasswordIcon) {
    showPasswordIcon.addEventListener('click', () => {
      hidePasswordIcon.style.display = 'block'
      showPasswordIcon.style.display = 'none'
      passwordInputField.setAttribute('type', 'password')
    })
  }

  if (hidePasswordIcon) {
    hidePasswordIcon.addEventListener('click', () => {
      hidePasswordIcon.style.display = 'none'
      showPasswordIcon.style.display = 'block'
      passwordInputField.setAttribute('type', 'text')
    })
  }
}

//Футер
function initFooterJS() {
  document.querySelector('.copyright').textContent +=
    ' ' + new Date().getFullYear()
}
