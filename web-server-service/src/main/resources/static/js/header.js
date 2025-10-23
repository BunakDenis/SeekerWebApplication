const userEmail = "True_Finder@mystic-school.ru";
const userPassword = "incompleteness2025";

// Изменение цвета логотипа Єгрегора и названия Школы при наведении или на логотип или на текст
const alVadudIcon = document.querySelector(".al-vadud");
const shcoolName = document.querySelector(".school-name");
alVadudIcon.addEventListener("mouseenter", function () {
  changeLogoColor(true);
});

alVadudIcon.addEventListener("mouseleave", function () {
  changeLogoColor(false);
});

shcoolName.addEventListener("mouseenter", function () {
  changeLogoColor(true);
});

shcoolName.addEventListener("mouseleave", function () {
  changeLogoColor(false);
});

function changeLogoColor(isFocuse) {
  const alVadudIcon = document.querySelector(".al-vadud");
  const shcoolName = document.querySelector(".school-name");
  if (isFocuse) {
    shcoolName.style.color = "gold";
    alVadudIcon.style.fill = "gold";
  } else {
    shcoolName.style.color = "red";
    alVadudIcon.style.fill = "red";
  }
}

//Контейнер поиска по сайту
const pageHeader = document.getElementById("page-header");

// Дебаунс для оптимизации количества запросов
function debounce(func, delay) {
  return function (...args) {
    clearTimeout(debounceTimeout);
    debounceTimeout = setTimeout(() => func.apply(this, args), delay);
  };
}

window.addEventListener("resize", () => {
  debounce(setNavLinksPanelPosition(), 150);
  debounce(setSearchFieldPosition(), 150);
  debounce(setUserProfileInfoPoupPosition(), 150);
  debounce(setSignInPoupPosition(), 150);
  debounce(setNotificationPopupPosition(), 150);
  debounce(setNavLinksPanelToggleIconPosition(), 150);
});

//Глобальный слушатель для модальных форм
document.addEventListener("click", (e) => {
  const target = e.target;

  console.log("target = ", target);

  //Формы авторизации и информации о юзере
  const userProfileIcon = document.getElementById("user-profile-icon");
  const signInContainer = document.querySelector(".sign-in-container");
  const userInformationContainer = document.querySelector(
    ".user-information-container"
  );

  if (target.classList.contains("user-information-form-cancel")) {
    userInformationContainer.classList.remove("show");
    userProfileIcon.classList.remove("active");
  }

  if (target.classList.contains("sign-in-form-cancel")) {
    signInContainer.classList.remove("show");
    userProfileIcon.classList.remove("active");
  }

  /*
    Переключение форм входа юзера и информации о юзере. При нажатии на кнопку "выйти" и последующем обновлении страницы в информации о юзере будет 
    отображаться форма регистрации юзера.
    При нажатии на кнопку "войти" в форме регистрации юзера, при следующем обновлении страницы будет отображаться форма информации о юзере
*/

  /* istanbul ignore next */
  if (target.classList.contains("sign-out-link")) {
    userAuthorization = false;
  }

  /* istanbul ignore next */
  if (target.classList.contains("sign-in-button")) {
    const inputUserEmail = document.querySelector(
      "#sign-in-user-email-input"
    ).value;
    const inputPassword = document.querySelector(
      "#sign-in-password-input"
    ).value;

    if (inputUserEmail === userEmail && inputPassword === userPassword) {
      localStorage.setItem(userAuthorizationKey, "true");
    } else {
      alert("Неверный логин или пароль");
    }
  }

  //Лисенер уведомлений
  const notificationIcon = document.getElementById("notification-icon");
  const notificationContainer = document.querySelector(
    ".notification-container"
  );

  /* istanbul ignore next */
  if (target.classList.contains("notification-form-cancel")) {
    notificationContainer.classList.remove("show");
    notificationIcon.classList.remove("active");
  }
});

pageHeader.addEventListener("click", (e) => {
  const target = e.target;
  console.log("target = ", target);

  //Лисенеры переключения видимости меню навбара на мобильном екране
  const navLinksContainer = document.querySelector(".nav-links-container");
  const navLinksToggleIcon = document.getElementById("nav-links-toggle");

  if (target.id === "nav-links-toggle") {
    if (navLinksContainer.classList.contains("hide")) {
      navLinksContainer.classList.remove("hide");
      navLinksToggleIcon.classList.add("bi-arrow-up-square-fill");
      navLinksToggleIcon.classList.remove("bi-arrow-down-square-fill");
    } else {
      navLinksContainer.classList.add("hide");
      navLinksToggleIcon.classList.remove("bi-arrow-up-square-fill");
      navLinksToggleIcon.classList.add("bi-arrow-down-square-fill");
    }

    changeNavLinksPanelToggleIconPosition();
  }

  //Лисенеры иконок поиска по сайту и деактивации окна поиска
  const searchContainer = document.querySelector(".search-container");

  if (target.id === "search-site-icon") {
    if (target.classList.contains("bi-search")) {
      searchContainer.classList.add("show");
      target.classList.remove("bi-search");
      target.classList.add("bi-x-lg");
      target.setAttribute("title", "Закрыть окно поиска");
    } else if (target.classList.contains("bi-x-lg")) {
      searchContainer.classList.remove("show");
      target.classList.remove("bi-x-lg");
      target.classList.add("bi-search");
      target.setAttribute("title", "Поиск по сайту");
    }
  }

  if (target.id === "search-site-close-icon") {
    const searchIcon = document.getElementById("search-site-icon");
    const closeSearchFieldIcon = document.getElementById(
      "search-site-close-icon"
    );
    const inputField = document.getElementById("input-search-field");

    inputField.value = "";

    searchContainer.classList.remove("show");
    searchIcon.classList.remove("hide");
    closeSearchFieldIcon.classList.add("hide");
    closeSearchFieldIcon.classList.remove("active");
  }

  //Лисенеры иконок входа и профиля юзера
  const userProfileIcon = document.getElementById("user-profile-icon");
  const signInContainer = document.querySelector(".sign-in-container");
  const userInformationContainer = document.querySelector(
    ".user-information-container"
  );

  if (target.id === "user-profile-icon") {
    if (userAuthorization) {
      if (userInformationContainer.classList.contains("show")) {
        userInformationContainer.classList.remove("show");
        userProfileIcon.classList.remove("active");
      } else {
        userInformationContainer.classList.add("show");
        userProfileIcon.classList.add("active");
      }
    } else {
      if (signInContainer.classList.contains("show")) {
        signInContainer.classList.remove("show");
        userProfileIcon.classList.remove("active");
      } else {
        signInContainer.classList.add("show");
        userProfileIcon.classList.add("active");
      }
    }
  }

  //Лисенер иконки уведомлений
  const notificationIcon = document.getElementById("notification-icon");
  const notificationContainer = document.querySelector(
    ".notification-container"
  );

  if (target.id === "notification-icon") {
    if (notificationContainer.classList.contains("show")) {
      notificationContainer.classList.remove("show");
      notificationIcon.classList.remove("active");
    } else {
      notificationContainer.classList.add("show");
      notificationIcon.classList.add("active");
    }
  }
});

setNavLinksPanelPosition();
setSearchFieldPosition();
setUserProfileInfoPoupPosition();
setSignInPoupPosition();
setNotificationPopupPosition();
setNavLinksPanelToggleIconPosition();

function setSearchFieldPosition() {
  const windowWidth = window.innerWidth;
  const searchSiteIcon = document.getElementById("search-site-icon");
  const searchContainer = document.querySelector(".search-container");

  setHeaderPoupPosition(searchSiteIcon, searchContainer);

  searchContainer.style.right = "5vw";

  /*
    console.log('window.innerWidth =', window.innerWidth)
    console.log('Ширина элемента =', navIconContainerRect.width)
    console.log('Отступ справа =', window.innerWidth - navIconContainerRect.right)
    console.log('searchContainer.style.top=' + searchContainer.style.top)
    console.log('searchContainer.style.right=' + searchContainer.style.right)
    */
}

function setUserProfileInfoPoupPosition() {
  const userProfileIcon = document.getElementById("user-profile-icon");
  const userInformationCnt = document.getElementById(
    "user-information-container"
  );

  setHeaderPoupPosition(userProfileIcon, userInformationCnt);

  /*
    console.log('window.innerWidth =', window.innerWidth)
    console.log('Ширина элемента =', navIconContainerRect.width)
    console.log('Отступ справа =', window.innerWidth - navIconContainerRect.right)
    console.log('searchContainer.style.top=' + searchContainer.style.top)
    console.log('searchContainer.style.right=' + searchContainer.style.right)
    */
}

function setSignInPoupPosition() {
  const userProfileIcon = document.getElementById("user-profile-icon");
  const signInCnt = document.getElementById("sign-in-container");

  setHeaderPoupPosition(userProfileIcon, signInCnt);

  /*
    console.log('window.innerWidth =', window.innerWidth)
    console.log('Ширина элемента =', navIconContainerRect.width)
    console.log('Отступ справа =', window.innerWidth - navIconContainerRect.right)
    console.log('searchContainer.style.top=' + searchContainer.style.top)
    console.log('searchContainer.style.right=' + searchContainer.style.right)
    */
}

function setNotificationPopupPosition() {
  const notificationIcon = document.getElementById("notification-icon");
  const notificationContainer = document.querySelector(
    ".notification-container"
  );

  setHeaderPoupPosition(notificationIcon, notificationContainer);
}

function setNavLinksPanelPosition() {
  const navLinksContainer = document.querySelector(".nav-links-container");
  const windowWidth = window.innerWidth;
  const headerRect = getHeaderRect();
  const sm = getBreakpoint("sm");

  if (windowWidth <= sm) {
    navLinksContainer.style.top = headerRect.bottom + scrollY + "px";
  }
}

function setNavLinksPanelToggleIconPosition() {
  const navLinksPanelToggle = document.getElementById("nav-links-toggle");
  const navLink = document.querySelectorAll(".nav-link");

  const scrollX = window.scrollX || document.documentElement.scrollLeft;

  const navLinkRect = navLink[0].getBoundingClientRect();

  navLinksPanelToggle.style.top = navLinkRect.bottom + scrollX - 5 + "px";
}

function changeNavLinksPanelToggleIconPosition() {
  // Получаем header и иконку; безопасно выходим, если чего-то нет
  const header =
    document.getElementById("page-header") || document.querySelector("header");
  const icon = document.getElementById("nav-links-toggle");

  if (!header || !icon) return;

  if (icon.classList.contains("bi-arrow-down-square-fill")) {
    // Получаем rect'ы относительно viewport
    const headerRect = header.getBoundingClientRect();
    const iconRect = icon.getBoundingClientRect();

    // distance = расстояние от нижней границы header до верхней границы иконки (в координатах viewport)
    // если icon находится ниже header, distance будет положительным
    const distance = iconRect.top - headerRect.bottom; // число в пикселях (может быть отрицательным)

    // Если ты хотел оригинальную цель: transform = translateY(-(navTop - (headerBottom + scrollY)))
    // можно просто использовать distance и применить с нужным знаком.
    // Здесь логично смещать иконку вверх на расстояние distance → translateY(-distance).
    const translateY = -distance;

    // Применим transform. Явно добавляем 'px' и используем requestAnimationFrame
    icon.style.transform = `translateY(${translateY - 15}px)`;
  } else if (icon.classList.contains("bi-arrow-up-square-fill")) {
    icon.style.transform = `translateY(0)`;
  }
}

function setHeaderPoupPosition(baseIcon, popupContainer) {
  const headerRect = getHeaderRect();
  const iconRect = baseIcon.getBoundingClientRect();

  const scrollY = window.scrollY || document.documentElement.scrollTop;
  const scrollX = window.scrollX || document.documentElement.scrollLeft;

  const sm = getBreakpoint("sm");
  const width = window.innerWidth;

  // Базовое значение отступа от низа шапки
  let topOffset = headerRect.bottom + scrollY + 10;

  if (width <= sm) {
    const navLinksContainer = document.querySelector(".nav-links-container");

    if (navLinksContainer) {
      const navLinksRect = navLinksContainer.getBoundingClientRect();
      // Добавляем высоту навигации, если она отображается при малом экране
      topOffset += navLinksRect.height;
    }
  }

  popupContainer.style.top = `${topOffset}px`;
  popupContainer.style.right = `${
    scrollX + window.innerWidth - iconRect.right
  }px`;
}

function getHeaderRect() {
  const pageHeader = document.querySelector(".site-navbar-wrap");

  return pageHeader.getBoundingClientRect();
}

function getBreakpoint(name) {
  const value = getComputedStyle(document.documentElement)
    .getPropertyValue(`--breakpoint-${name}`)
    .trim();
  return parseInt(value, 10);
}

//------------------------------------------

// Переключатель меню входа юзера и меню уведомлений
// Переменная для определения авторизации пользователя
let userAuthorization = false;
const userAuthorizationKey = "userAuthorization";

/*
  if (localStorage.getItem(userAuthorizationKey)) {
    userAuthorization = localStorage.getItem('userAuthorization')
  } else {
    userAuthorization = false
  }
*/
