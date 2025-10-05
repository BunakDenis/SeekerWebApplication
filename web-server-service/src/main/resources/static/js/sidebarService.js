// Предполагается, что contentService определен где-то еще и доступен глобально или импортирован.
// Пример заглушки:
/*
const contentService = {
    activateContentItem: function(contentId) {
        console.log('Активация элемента контента:', contentId);
        // Здесь должна быть ваша реальная логика переключения контента
        // Пример: Скрыть все элементы контента, показать тот, у которого contentId
        // document.querySelectorAll('.content-item').forEach(item => item.style.display = 'none');
        // const itemToShow = document.getElementById(contentId);
        // if (itemToShow) {
        //     itemToShow.style.display = 'block';
        // } else {
        //     console.warn('Элемент контента не найден:', contentId);
        // }
    }
};
*/

/**
 * Сервис для управления общей видимостью контейнера сайдбара и анимацией его переключения.
 * Обрабатывает слушатель кнопки переключения и инициализирует слушатели для пунктов меню,
 * делегируя логику активации ссылок и отображения контента другим сервисам.
 */
class SidebarService {
  // Статические свойства для элементов DOM
  static sidebarContainer = null
  static sidebarToggleVisibilityBtn = null
  static cabinetContentContainer = null
  static sidebarMenuLiItems = null // Элементы LI для анимации при открытии/закрытии

  // Статические константы для анимации/стилей
  static STYLE_TRANSITION_DELAY = 100
  static STYLE_TRANSFORM_IF_MENU_SHOW = 'translateX(0)'
  static STYLE_TRANSFORM_IF_MENU_HIDE = 'translateX(-50%)'

  /**
   * Инициализирует SidebarService, находя необходимые элементы DOM.
   * Слушатели привязываются в отдельном методе attachListeners.
   */
  static init() {
    this._initElements()
    // Слушатели привязываются после инициализации всех сервисов
  }

  /**
   * Находит и сохраняет ссылки на необходимые элементы DOM.
   * @private
   */
  static _initElements() {
    this.sidebarContainer = document.getElementById('sidebar')
    this.sidebarToggleVisibilityBtn =
      document.getElementById('sidebar-toggle-btn')
    this.cabinetContentContainer = document.querySelector('.cabinet-content')
    if (this.sidebarContainer) {
      this.sidebarMenuLiItems = this.sidebarContainer.querySelectorAll(
        '.sidebar-menu-li-item'
      )
    }
  }

  /**
   * Привязывает слушатели событий к элементам, управляющим сайдбаром.
   */
  static attachListeners() {
    // Привязываем слушатель для кнопки переключения сайдбара
    if (this.sidebarToggleVisibilityBtn) {
      this.sidebarToggleVisibilityBtn.addEventListener('click', () =>
        this.toggle()
      )
    }
    // Слушатели для пунктов меню сайдбара привязываются в SideBarLinkService
  }

  /**
   * Переключает видимость сайдбара.
   */
  static toggle() {
    if (!this.sidebarContainer) return

    this._toggleClass(this.sidebarContainer, 'open')
    this._toggleClassChildrenElementsByParentClassName(
      this.sidebarContainer,
      this.sidebarMenuLiItems,
      'open',
      'show'
    )
    this._toggleCabinetContentWidth()
  }

  /**
   * Принудительно включает видимость сайдбара.
   */
  static show() {
    if (!this.sidebarContainer || this.isVisible()) return
    this.toggle() // Используем toggle для применения классов и анимации
  }

  /**
   * Принудительно отключает видимость сайдбара.
   */
  static hide() {
    if (!this.sidebarContainer || !this.isVisible()) return
    this.toggle() // Используем toggle для применения классов и анимации
  }

  /**
   * Проверяет, виден ли сайдбар в данный момент.
   * @returns {boolean} True, если виден, false в противном случае.
   */
  static isVisible() {
    if (!this.sidebarContainer) return false
    return this.sidebarContainer.classList.contains('open')
  }

  // --- Приватные вспомогательные методы ---

  /**
   * Переключает класс у заданного элемента.
   * @param {Element} element - Элемент DOM.
   * @param {string} className - Имя класса для переключения.
   * @private
   */
  static _toggleClass(element, className) {
    if (element && element.classList) {
      element.classList.toggle(className)
    }
  }

  /**
   * Переключает класс и применяет transform/transition к дочерним элементам
   * в зависимости от состояния класса родителя, используется для анимации пунктов сайдбара.
   * @param {Element} parent - Родительский элемент (контейнер сайдбара).
   * @param {NodeListOf<Element>} childrens - Дочерние элементы (LI пункты меню сайдбара).
   * @param {string} parentClassName - Имя класса у родителя ('open').
   * @param {string} childrenClassName - Имя класса для переключения у дочерних элементов ('show').
   * @private
   */
  static _toggleClassChildrenElementsByParentClassName(
    parent,
    childrens,
    parentClassName,
    childrenClassName
  ) {
    if (!parent || !childrens) return

    const isParentOpen = parent.classList.contains(parentClassName)

    childrens.forEach((child, i) => {
      if (isParentOpen) {
        child.classList.add(childrenClassName)
        child.style.transform = this.STYLE_TRANSFORM_IF_MENU_SHOW
        child.style.transitionDelay = i * this.STYLE_TRANSITION_DELAY + 'ms'
      } else {
        child.classList.remove(childrenClassName)
        child.style.transform = this.STYLE_TRANSFORM_IF_MENU_HIDE
        child.style.transitionDelay = '0ms'
      }
    })
  }

  /**
   * Корректирует класс ширины контейнера контента в зависимости от видимости сайдбара.
   * @private
   */
  static _toggleCabinetContentWidth() {
    if (!this.cabinetContentContainer) return

    if (this.isVisible()) {
      this.cabinetContentContainer.classList.remove('hidden-sidebar')
    } else {
      this.cabinetContentContainer.classList.add('hidden-sidebar')
    }
  }
}

/**
 * Сервис для управления активным состоянием и эффектами наведения ссылок сайдбара
 * (основные пункты меню и подпункты).
 * Хранит ссылку на активный пункт меню сайдбара.
 */
class SideBarLinkService {
  // Статические свойства для элементов DOM
  static sidebarMenuItems = null
  static subMenuItems = null
  static ulSubMenuItems = null // Коллекция всех UL подменю (.serv-show)

  // Статические свойства для хранения активных элементов
  // Примечание: Активная ссылка сайдбара (после клика) теперь хранится в CntCaptionLinkService
  // Этот сервис управляет только визуальным состоянием ссылок сайдбара (active class, hover effects)

  // Статические константы для ключей localStorage, связанных с состоянием наведения
  // Состояние наведения (какое подменю открыто по наведению) можно оставить в localStorage,
  // если нужно сохранять его при перезагрузке, или перенести в статическое свойство,
  // если состояние наведения сбрасывается при перезагрузке. Оставляем в localStorage по аналогии с исходным кодом.
  static VISIBLE_SIDEBAR_MENU_ID_KEY = 'visible-sidebar-menu-id'

  /**
   * Инициализирует SideBarLinkService, находя необходимые элементы DOM.
   * Слушатели привязываются в отдельном методе attachListeners.
   */
  static init() {
    this._initElements()
    // Слушатели привязываются после инициализации всех сервисов
  }

  /**
   * Находит и сохраняет ссылки на необходимые элементы DOM.
   * @private
   */
  static _initElements() {
    const sidebarContainer = document.getElementById('sidebar')
    if (sidebarContainer) {
      this.sidebarMenuItems =
        sidebarContainer.querySelectorAll('.sidebar-menu-item')
      this.subMenuItems = sidebarContainer.querySelectorAll(
        '.sidebar-submenu-item'
      )
      this.ulSubMenuItems = sidebarContainer.querySelectorAll('.serv-show')
    }
  }

  /**
   * Привязывает слушатели событий к элементам меню и подменю сайдбара.
   */
  static attachListeners() {
    // Привязываем слушатели для основных пунктов меню сайдбара
    if (this.sidebarMenuItems) {
      this.sidebarMenuItems.forEach(item => {
        // Слушатель клика для основных пунктов меню
        item.addEventListener('click', event => {
          event.preventDefault()
          // Делегируем обработку клика сервису ссылок контента
          CntCaptionLinkService.activateContentForLink(item) // Активируем контент и связанные ссылки
        })

        // Слушатели наведения для основных пунктов меню (показ/скрытие подменю)
        item.addEventListener('mouseenter', () =>
          this.handleLinkMouseEnter(item)
        )

        // Находим связанный UL подменю для слушателя mouseleave
        const ulSubMenuItem = this._getSidebarMenuUlElement(item.id)
        if (ulSubMenuItem) {
          item.addEventListener('mouseleave', () =>
            this.handleLinkMouseLeave(item, ulSubMenuItem)
          )
        }
      })
    }

    // Привязываем слушатели для подпунктов меню сайдбара
    if (this.subMenuItems) {
      this.subMenuItems.forEach(subMenuItem => {
        // Слушатель клика для подпунктов меню
        subMenuItem.addEventListener('click', event => {
          event.preventDefault()
          // Делегируем обработку клика сервису ссылок контента
          CntCaptionLinkService.activateContentForLink(subMenuItem) // Активируем контент и связанные ссылки
        })
      })
    }

    // Слушатель mouseleave для контейнеров UL подменю (.serv-show)
    if (this.ulSubMenuItems) {
      this.ulSubMenuItems.forEach(ulSubMenuItem => {
        ulSubMenuItem.addEventListener('mouseleave', () =>
          this.handleSubmenuMouseLeave(ulSubMenuItem)
        )
      })
    }
  }

  /**
   * Обрабатывает событие mouseenter для основного пункта меню сайдбара.
   * Показывает соответствующее подменю и применяет классы, связанные с наведением.
   * @param {Element} linkElement - Элемент ссылки основного меню сайдбара, на который навели курсор.
   */
  static handleLinkMouseEnter(linkElement) {
    if (!linkElement) return

    const ulElement = this._getSidebarMenuUlElement(linkElement.id)
    const liElement = this._getSidebarMenuLiElement(linkElement.id)

    if (ulElement && liElement) {
      // Деактивируем ранее наведенное меню (если оно другое)
      const previouslyHoveredId = localStorage.getItem(
        this.VISIBLE_SIDEBAR_MENU_ID_KEY
      )
      if (previouslyHoveredId && previouslyHoveredId !== linkElement.id) {
        const previouslyHoveredUl =
          this._getSidebarMenuUlElement(previouslyHoveredId)
        const previouslyHoveredLi =
          this._getSidebarMenuLiElement(previouslyHoveredId)
        if (previouslyHoveredUl)
          previouslyHoveredUl.classList.remove('show-menu')
        if (previouslyHoveredLi) previouslyHoveredLi.classList.remove('active') // Удаляем класс active с LI при уходе курсора
        this._changeVisibilityOfSubMenuItems(previouslyHoveredUl, false) // Скрываем пункты подменю
      }

      // Активируем текущее наведенное меню
      ulElement.classList.add('show-menu')
      liElement.classList.add('active') // Добавляем класс active к LI при наведении
      localStorage.setItem(this.VISIBLE_SIDEBAR_MENU_ID_KEY, linkElement.id)
      this._changeVisibilityOfSubMenuItems(ulElement, true) // Показываем пункты подменю с анимацией
    }
  }

  /**
   * Обрабатывает событие mouseleave для основного пункта меню сайдбара.
   * Скрывает подменю и удаляет классы, связанные с наведением, если курсор
   * не перешел в связанный UL подменю.
   * @param {Element} linkElement - Элемент ссылки основного меню сайдбара, с которого ушел курсор.
   * @param {Element} ulElement - Связанный элемент UL подменю.
   */
  static handleLinkMouseLeave(linkElement, ulElement) {
    if (!linkElement || !ulElement) return

    const liElement = this._getSidebarMenuLiElement(linkElement.id)

    // Проверяем, находится ли курсор мыши все еще над ссылкой или связанным UL подменю
    // Воспроизводим логику исходного кода с использованием псевдокласса :hover
    if (
      liElement &&
      !linkElement.matches(':hover') &&
      !ulElement.matches(':hover')
    ) {
      ulElement.classList.remove('show-menu')
      liElement.classList.remove('active') // Удаляем класс active с LI при уходе курсора
      // localStorage.removeItem(this.VISIBLE_SIDEBAR_MENU_ID_KEY); // Не очищаем здесь, только когда наводится на новый элемент
      this._changeVisibilityOfSubMenuItems(ulElement, false) // Скрываем пункты подменю с анимацией
    }
  }

  /**
   * Обрабатывает событие mouseleave для контейнера UL подменю (`.serv-show`).
   * Скрывает подменю и удаляет классы, связанные с наведением, с родительского LI.
   * @param {Element} ulElement - Элемент UL подменю, с которого ушел курсор.
   */
  static handleSubmenuMouseLeave(ulElement) {
    if (!ulElement) return

    const liElement = ulElement.parentElement // LI, содержащий UL

    if (liElement) {
      ulElement.classList.remove('show-menu')
      liElement.classList.remove('active') // Удаляем класс active с LI
      // localStorage.removeItem(this.VISIBLE_SIDEBAR_MENU_ID_KEY); // Не очищаем здесь
      this._changeVisibilityOfSubMenuItems(ulElement, false) // Скрываем пункты подменю с анимацией
    }
  }

  /**
   * Активирует визуальное состояние ссылки сайдбара (добавляет класс 'active' к ссылке и ее родительскому LI).
   * Используется CntCaptionLinkService при активации пары ссылок.
   * @param {Element} linkElement - Элемент ссылки сайдбара для активации.
   */
  static activateLink(linkElement) {
    if (!linkElement) return
    const liElement = this._getSidebarMenuLiElement(linkElement.id)
    this._toggleActiveClass(Array.of(linkElement, liElement), true)
  }

  /**
   * Деактивирует визуальное состояние ссылки сайдбара (удаляет класс 'active' с ссылки и ее родительского LI).
   * Используется CntCaptionLinkService при деактивации ранее активной пары ссылок.
   * @param {Element} linkElement - Элемент ссылки сайдбара для деактивации.
   */
  static deactivateLink(linkElement) {
    if (!linkElement) return
    const liElement = this._getSidebarMenuLiElement(linkElement.id)
    this._toggleActiveClass(Array.of(linkElement, liElement), false)
  }

  // --- Приватные вспомогательные методы ---

  /**
   * Получает элемент UL подменю (`.serv-show`), связанный со ссылкой сайдбара.
   * @param {string} linkId - ID ссылки сайдбара.
   * @returns {Element|null} Элемент UL или null, если не найден.
   * @private
   */
  static _getSidebarMenuUlElement(linkId) {
    const link = document.getElementById(linkId)
    if (!link) return null

    if (
      link.classList.contains('serv-btn') ||
      link.classList.contains('sidebar-menu-item')
    ) {
      // Основной пункт меню
      return link.closest('li')?.querySelector('.serv-show')
    } else if (link.classList.contains('sidebar-submenu-item')) {
      // Пункт подменю
      return link.closest('li')?.closest('.serv-show') // Находим UL, содержащий LI пункта подменю
    }
    return null
  }

  /**
   * Получает родительский элемент LI ссылки сайдбара.
   * @param {string} linkId - ID ссылки сайдбара.
   * @returns {Element|null} Элемент LI или null, если не найден.
   * @private
   */
  static _getSidebarMenuLiElement(linkId) {
    const link = document.getElementById(linkId)
    if (!link) return null
    return link.closest('li')
  }

  /**
   * Управляет видимостью и анимацией пунктов подменю внутри UL.
   * @param {Element} ulElement - Элемент UL подменю (`.serv-show`).
   * @param {boolean} isVisible - True для показа, false для скрытия.
   * @private
   */
  static _changeVisibilityOfSubMenuItems(ulElement, isVisible) {
    if (!ulElement) return

    const links = ulElement.querySelectorAll('.sidebar-submenu-item')
    const styleTransitionDelay = SidebarService.STYLE_TRANSITION_DELAY
    // Корректные transform на основе использования в исходном коде
    const styleTransformShow = SidebarService.STYLE_TRANSFORM_IF_MENU_SHOW // translateX(0)
    const styleTransformHide = SidebarService.STYLE_TRANSFORM_IF_MENU_HIDE // translateX(-50%)

    links.forEach((link, i) => {
      const parentLi = link.parentElement // LI, содержащий ссылку подпункта меню
      if (parentLi) {
        if (isVisible) {
          parentLi.style.visibility = 'visible'
          parentLi.style.opacity = '1'
          parentLi.style.lineHeight = '4vh' // Исходный код использует 4vh

          link.style.opacity = '1'
          link.style.visibility = 'visible'
          link.style.transform = styleTransformShow // Анимация появления
          link.style.transitionDelay = i * styleTransitionDelay + 'ms'
        } else {
          parentLi.style.visibility = 'hidden'
          parentLi.style.opacity = '0'
          parentLi.style.lineHeight = '0' // Сворачиваем высоту

          link.style.opacity = '0'
          link.style.visibility = 'hidden'
          link.style.transform = styleTransformHide // Анимация исчезновения
          link.style.transitionDelay = '0ms' // Сбрасываем задержку при скрытии
        }
      }
    })
  }

  /**
   * Вспомогательный метод для добавления или удаления класса 'active' из списка элементов.
   * @param {Array<Element|null>} elementList - Список элементов для изменения.
   * @param {boolean} isActivate - True для добавления 'active', false для удаления.
   * @private
   */
  static _toggleActiveClass(elementList, isActivate) {
    elementList.forEach(element => {
      if (element && element.classList) {
        if (isActivate) {
          element.classList.add('active')
        } else {
          element.classList.remove('active')
        }
      }
    })
  }
}

/**
 * Сервис для управления активным состоянием ссылок в заголовке контента
 * и запуска отображения соответствующих блоков контента.
 * Хранит ссылки на активные элементы меню и подменю контента.
 */
class CntCaptionLinkService {
  // Статические свойства для элементов DOM
  static contentControlsMenuContainer = null
  static contentMenuLinks = null
  static contentSubmenuLinks = null

  // Статические свойства для хранения активных элементов (заменяют localStorage для активного состояния)
  static activeSidebarLink = null // Активная ссылка сайдбара (основное меню)
  static activeContentMenuLink = null // Активная основная ссылка в заголовке контента
  static activeContentSubMenuLink = null // Активная ссылка подменю в заголовке контента (Духовная/Мистическая работа)
  static activeContentMenuContainer = null // Активный контейнер меню в заголовке контента

  // Статические константы для ключей localStorage (оставлены только те, что не связаны с активным состоянием после клика)
  // Исходный код использовал эти ключи для хранения активного состояния.
  // Теперь активные элементы хранятся в статических свойствах класса.
  // Ключи localStorage удалены, так как они больше не используются для хранения активных элементов.

  /**
   * Инициализирует CntCaptionLinkService, находя необходимые элементы DOM.
   * Слушатели привязываются в отдельном методе attachListeners.
   */
  static init() {
    this._initElements()
    // Слушатели привязываются после инициализации всех сервисов
  }

  /**
   * Находит и сохраняет ссылки на необходимые элементы DOM.
   * @private
   */
  static _initElements() {
    this.contentControlsMenuContainer =
      document.querySelector('.cnt-menu-controls')
    // Находим коллекции ссылок в заголовке контента для привязки слушателей
    this.contentMenuLinks = document.querySelectorAll('.cnt-menu-link')
    this.contentSubmenuLinks = document.querySelectorAll(
      '.cnt-menu-link-submenu-item'
    )
  }

  /**
   * Привязывает слушатели событий к элементам в заголовке контента.
   */
  static attachListeners() {
    // Привязываем слушатели для основных ссылок меню в заголовке контента
    if (this.contentMenuLinks) {
      this.contentMenuLinks.forEach(link => {
        link.addEventListener('click', event => {
          event.preventDefault()
          // Делегируем обработку клика сервису ссылок контента
          this.activateContentForLink(link) // Передаем элемент ссылки контента
        })
      })
    }

    // Привязываем слушатели для ссылок подменю в заголовке контента (например, Духовная/Мистическая работа)
    if (this.contentSubmenuLinks) {
      this.contentSubmenuLinks.forEach(link => {
        link.addEventListener('click', event => {
          event.preventDefault()
          // Делегируем обработку клика сервису ссылок контента
          // Клик по ссылке подменю контента должен активировать только само подменю
          this._toggleActiveSubMenuLinks(link)
          // Активируем соответствующий контент
          const contentId = this._getCntIdByLinkId(link.id) // Получаем ID контента из ID ссылки подменю контента
          if (
            typeof contentService !== 'undefined' &&
            contentService.activateContentItem
          ) {
            contentService.activateContentItem(contentId)
          } else {
            console.warn(
              'contentService или activateContentItem недоступен. Невозможно активировать контент для ID:',
              contentId
            )
          }
        })
      })
    }
  }

  /**
   * Активирует контент и соответствующие ссылки в заголовке на основе кликнутой ссылки
   * (может быть как ссылка сайдбара, так и ссылка в заголовке контента).
   * Это основная точка входа для обработки эффекта клика по ссылке на контент.
   * @param {Element} clickedElement - Элемент, по которому кликнули (ссылка сайдбара или ссылка в заголовке контента).
   */
  static activateContentForLink(clickedElement) {
    if (!clickedElement) return

    // 1. Переключаем активные классы на паре основных ссылок (сайдбар + заголовок контента)
    // Этот метод также обрабатывает деактивацию ранее активной пары и обновляет статические свойства
    // activeSidebarLink и activeContentMenuLink.
    const { newSbMenuLink } = this._toggleActiveMenuLinks(clickedElement)

    // Если удалось определить соответствующую основную ссылку сайдбара (даже если клик был по ссылке контента)
    if (newSbMenuLink) {
      // 2. Переключаем активный класс на специфических ссылках подменю заголовка контента (Духовная/Мистическая работа)
      // на основе кликнутой ссылки сайдбара. Обновляет статическое свойство activeContentSubMenuLink.
      this._toggleActiveSubMenuLinks(newSbMenuLink) // Используем ссылку сайдбара для определения подменю

      // 3. Переключаем видимость/активное состояние правильного контейнера меню контента в заголовке.
      // Обновляет статическое свойство activeContentMenuContainer.
      this._toggleVisibilityOfContentMenuContainer(newSbMenuLink) // Используем ссылку сайдбара для определения контейнера

      // 4. Запускаем отображение фактического элемента контента с использованием внешнего contentService.
      const contentId = this._getCntIdByLinkId(newSbMenuLink.id) // Получаем ID контента из ID ссылки сайдбара
      if (
        typeof contentService !== 'undefined' &&
        contentService.activateContentItem
      ) {
        contentService.activateContentItem(contentId)
      } else {
        console.warn(
          'contentService или activateContentItem недоступен. Невозможно активировать контент для ID:',
          contentId
        )
      }
    } else {
      console.warn(
        'Не удалось определить соответствующую основную ссылку сайдбара для кликнутого элемента:',
        clickedElement
      )
    }
  }

  // --- Приватные вспомогательные методы ---

  /**
   * Определяет базовое имя меню (например, 'home', 'spiritual-work') из ID ссылки сайдбара.
   * @param {Element} link - Элемент ссылки сайдбара.
   * @returns {string|null} Имя меню или null, если не найдено.
   * @private
   */
  static _defineActiveMenuNameBySidebarLink(link) {
    if (!link || !link.id) return null

    // Если это пункт подменю, сначала находим родительский пункт основного меню
    if (link.classList.contains('sidebar-submenu-item')) {
      const menuLink = link
        .closest('.serv-show') // Находим UL
        ?.closest('li') // Находим родительский LI для UL
        ?.querySelector('.sidebar-menu-item') // Находим ссылку основного меню внутри LI

      if (menuLink && menuLink.id) {
        const match = menuLink.id.match(/link-(.+)/)
        return match ? match[1] : null
      }
      return null // Не удалось найти родительскую ссылку меню
    }

    // Если это пункт основного меню
    const match = link.id.match(/link-(.+)/)
    return match ? match[1] : null
  }

  /**
   * Переключает класс 'active' у контейнера меню контента в заголовке,
   * соответствующего кликнутой ссылке сайдбара.
   * Обновляет статическое свойство activeContentMenuContainer.
   * @param {Element} sidebarLink - Элемент ссылки сайдбара.
   * @private
   */
  static _toggleVisibilityOfContentMenuContainer(sidebarLink) {
    if (!this.contentControlsMenuContainer) return

    const selectedMenuName =
      this._defineActiveMenuNameBySidebarLink(sidebarLink)

    if (!selectedMenuName) {
      console.warn(
        'Не удалось определить имя меню для ссылки сайдбара:',
        sidebarLink
      )
      return
    }

    const menuContainerId = 'cnt-menu-' + selectedMenuName
    const contentMenuContainer = document.getElementById(menuContainerId)

    this.contentControlsMenuContainer.classList.add('show') // Убеждаемся, что контейнер элементов управления виден

    // Деактивируем ранее активный контейнер меню контента
    if (
      this.activeContentMenuContainer &&
      this.activeContentMenuContainer !== contentMenuContainer
    ) {
      this.activeContentMenuContainer.classList.remove('active')
    }

    // Активируем новый контейнер меню контента
    if (contentMenuContainer) {
      contentMenuContainer.classList.add('active')
      this.activeContentMenuContainer = contentMenuContainer // Сохраняем ссылку на активный элемент
    } else {
      console.warn('Контейнер меню контента не найден для ID:', menuContainerId)
      // Опционально сбрасываем активное состояние, если контейнер не найден
      this.activeContentMenuContainer = null
    }
  }

  /**
   * Переключает класс 'active' у специфических ссылок подменю заголовка контента
   * (например, "Духовная Работа" / "Мистическая работа") на основе кликнутой ссылки сайдбара.
   * Обновляет статическое свойство activeContentSubMenuLink.
   * @param {Element} sidebarLink - Элемент ссылки сайдбара.
   * @private
   */
  static _toggleActiveSubMenuLinks(sidebarLink) {
    // Находим специфические ссылки подменю контента в заголовке
    const spiritalWorkMenu = document.getElementById(
      'cnt-link-submenu-spiritual-work'
    )
    const mistickWorkMenu = document.getElementById(
      'cnt-link-submenu-mystic-work'
    )
    const spiritalWorkAdmMenu = document.getElementById(
      'cnt-link-submenu-adm-spiritual-work'
    )
    const mistickWorkAdmMenu = document.getElementById(
      'cnt-link-submenu-adm-mystic-work'
    )

    // Сначала деактивируем все соответствующие ссылки подменю контента для простоты
    this._toggleActiveClass(
      [
        spiritalWorkMenu,
        mistickWorkMenu,
        spiritalWorkAdmMenu,
        mistickWorkAdmMenu
      ],
      false
    )

    // Определяем новую ссылку подменю контента для активации на основе ID кликнутой ссылки сайдбара
    let newContentSubMenuLink = null

    // Если кликнутая ссылка сайдбара связана с духовной работой (основная или подменю)
    if (sidebarLink.id.includes('spiritual-work')) {
      // Проверяем, является ли это админской ссылкой
      if (sidebarLink.id.includes('adm-')) {
        newContentSubMenuLink = spiritalWorkAdmMenu
      } else {
        newContentSubMenuLink = spiritalWorkMenu
      }
    } else if (sidebarLink.id.includes('mystic-work')) {
      // Если кликнутая ссылка сайдбара связана с мистической работой (основная или подменю)
      // Проверяем, является ли это админской ссылкой
      if (sidebarLink.id.includes('adm-')) {
        newContentSubMenuLink = mistickWorkAdmMenu
      } else {
        newContentSubMenuLink = mistickWorkMenu
      }
    }
    // Если ссылка не связана с духовной/мистической работой, newContentSubMenuLink остается null,
    // и ни одна ссылка подменю контента не будет активирована (уже деактивированы выше).

    // Активируем новую ссылку подменю контента, если найдена
    if (newContentSubMenuLink) {
      this._toggleActiveClass([newContentSubMenuLink], true)
      this.activeContentSubMenuLink = newContentSubMenuLink // Сохраняем ссылку на активный элемент
    } else {
      // Если ни одна ссылка подменю контента не соответствует кликнутой ссылке, убеждаемся, что ни одна не активна.
      this.activeContentSubMenuLink = null
    }
  }

  /**
   * Управляет классом 'active' для кликнутой ссылки (сайдбар или контент) и ее соответствующей
   * основной ссылки в заголовке контента, деактивируя ранее активную пару.
   * Обновляет статические свойства activeSidebarLink и activeContentMenuLink.
   * @param {Element} clickedElement - Элемент, по которому кликнули (ожидается ссылка сайдбара или ссылка в заголовке контента).
   * @returns {{newSbMenuLink: Element|null, newContentMenuLink: Element|null}} Пара найденных ссылок или null.
   * @private
   */
  static _toggleActiveMenuLinks(clickedElement) {
    let newSbMenuLink = null
    let newContentMenuLink = null

    // Определяем пару ссылок на основе кликнутого элемента
    if (
      clickedElement.classList.contains('sidebar-menu-item') ||
      clickedElement.classList.contains('sidebar-submenu-item')
    ) {
      // Кликнули по ссылке сайдбара
      const menuName = this._defineActiveMenuNameBySidebarLink(clickedElement)
      if (!menuName) {
        console.warn(
          'Не удалось определить имя меню для ссылки сайдбара:',
          clickedElement
        )
        return { newSbMenuLink: null, newContentMenuLink: null }
      }
      // Если кликнули по подменю, newSbMenuLink должна быть родительской основной ссылкой меню
      if (clickedElement.classList.contains('sidebar-submenu-item')) {
        newSbMenuLink = clickedElement
          .closest('.serv-show')
          ?.closest('li')
          ?.querySelector('.sidebar-menu-item')
      } else {
        // Если кликнули по основной ссылке меню, newSbMenuLink - это сам элемент
        newSbMenuLink = clickedElement
      }

      newContentMenuLink = document.getElementById('cnt-link-' + menuName)
    } else if (clickedElement.classList.contains('cnt-menu-link')) {
      // Кликнули по ссылке в заголовке контента
      const menuName = clickedElement.id.substring(
        clickedElement.id.indexOf('link-') + 5
      )
      if (!menuName) {
        console.warn(
          'Не удалось определить имя меню для ссылки контента:',
          clickedElement
        )
        return { newSbMenuLink: null, newContentMenuLink: null }
      }
      newContentMenuLink = clickedElement
      newSbMenuLink = document.getElementById('link-' + menuName)
    } else {
      console.warn(
        'Кликнутый элемент не является распознанной ссылкой меню:',
        clickedElement
      )
      return { newSbMenuLink: null, newContentMenuLink: null } // Не ссылка, которую мы обрабатываем
    }

    // Проверяем, является ли кликнутая ссылка уже активной
    // Сравниваем по ссылке сайдбара, так как она является "первичной"
    if (
      this.activeSidebarLink &&
      newSbMenuLink &&
      this.activeSidebarLink.id === newSbMenuLink.id
    ) {
      // Уже активна, ничего не делаем согласно поведению исходного кода
      return { newSbMenuLink, newContentMenuLink } // Возвращаем текущую пару
    }

    // Деактивируем ранее активные ссылки (ссылка сайдбара, ее родительский LI, ссылка контента)
    if (this.activeSidebarLink) {
      SideBarLinkService.deactivateLink(this.activeSidebarLink) // Деактивируем ссылку сайдбара и ее LI через SideBarLinkService
    }
    if (this.activeContentMenuLink) {
      this._toggleActiveClass([this.activeContentMenuLink], false) // Деактивируем ссылку контента
    }

    // Активируем новые ссылки (ссылка сайдбара, ее родительский LI, ссылка контента)
    if (newSbMenuLink) {
      SideBarLinkService.activateLink(newSbMenuLink) // Активируем ссылку сайдбара и ее LI через SideBarLinkService
      this.activeSidebarLink = newSbMenuLink // Сохраняем ссылку на активный элемент
    } else {
      this.activeSidebarLink = null
    }

    if (newContentMenuLink) {
      this._toggleActiveClass([newContentMenuLink], true) // Активируем ссылку контента
      this.activeContentMenuLink = newContentMenuLink // Сохраняем ссылку на активный элемент
    } else {
      this.activeContentMenuLink = null
    }

    return { newSbMenuLink, newContentMenuLink } // Возвращаем новую пару
  }

  /**
   * Управляет классом 'active' для ссылок подменю в заголовке контента
   * (например, "Духовная Работа" / "Мистическая работа").
   * Обновляет статическое свойство activeContentSubMenuLink.
   * @param {Element} link - Элемент ссылки (обычно ссылка сайдбара, но может быть и ссылка подменю контента).
   * @private
   */
  static _toggleActiveSubMenuLinks(link) {
    // Находим специфические ссылки подменю контента в заголовке
    const spiritalWorkMenu = document.getElementById(
      'cnt-link-submenu-spiritual-work'
    )
    const mistickWorkMenu = document.getElementById(
      'cnt-link-submenu-mystic-work'
    )
    const spiritalWorkAdmMenu = document.getElementById(
      'cnt-link-submenu-adm-spiritual-work'
    )
    const mistickWorkAdmMenu = document.getElementById(
      'cnt-link-submenu-adm-mystic-work'
    )

    // Сначала деактивируем все соответствующие ссылки подменю контента для простоты
    this._toggleActiveClass(
      [
        spiritalWorkMenu,
        mistickWorkMenu,
        spiritalWorkAdmMenu,
        mistickWorkAdmMenu
      ],
      false
    )

    // Определяем новую ссылку подменю контента для активации на основе кликнутой ссылки
    let newContentSubMenuLink = null

    // Если кликнутый элемент - это ссылка подменю контента
    if (link.classList.contains('cnt-menu-link-submenu-item')) {
      newContentSubMenuLink = link
    }
    // Если кликнутый элемент - это ссылка сайдбара (основная или подменю)
    else if (
      link.classList.contains('sidebar-menu-item') ||
      link.classList.contains('sidebar-submenu-item')
    ) {
      // Сопоставляем шаблон ID ссылки сайдбара с ID ссылки подменю контента
      if (link.id.includes('spiritual-work')) {
        // Проверяем, является ли это админской ссылкой
        if (link.id.includes('adm-')) {
          newContentSubMenuLink = document.getElementById(
            'cnt-link-submenu-adm-spiritual-work'
          )
        } else {
          newContentSubMenuLink = document.getElementById(
            'cnt-link-submenu-spiritual-work'
          )
        }
      } else if (link.id.includes('mystic-work')) {
        // Проверяем, является ли это админской ссылкой
        if (link.id.includes('adm-')) {
          newContentSubMenuLink = document.getElementById(
            'cnt-link-submenu-adm-mystic-work'
          )
        } else {
          newContentSubMenuLink = document.getElementById(
            'cnt-link-submenu-mystic-work'
          )
        }
      }
      // Если ссылка не связана с духовной/мистической работой, newContentSubMenuLink остается null
    }
    // Если кликнутый элемент - это основная ссылка заголовка контента (.cnt-menu-link),
    // newContentSubMenuLink остается null, гарантируя, что ни одна ссылка подменю не активна.

    // Активируем новую ссылку подменю контента, если найдена
    if (newContentSubMenuLink) {
      this._toggleActiveClass([newContentSubMenuLink], true)
      this.activeContentSubMenuLink = newContentSubMenuLink // Сохраняем ссылку на активный элемент
    } else {
      // Если ни одна ссылка подменю контента не соответствует кликнутой ссылке, убеждаемся, что ни одна не активна.
      this.activeContentSubMenuLink = null
    }
  }

  /**
   * Сопоставляет ID ссылки (сайдбар или контент) с соответствующим ID элемента контента.
   * Воспроизводит логику из исходной функции `getCntIdByLinkId`.
   * @param {string} linkId - ID ссылки.
   * @returns {string|null} ID элемента контента или null.
   * @private
   */
  static _getCntIdByLinkId(linkId) {
    if (!linkId) return null

    let result
    // Эта логика, кажется, предполагает, что вход может быть ID контейнера контента ('cnt-menu-...')
    // и преобразует его, иначе просто добавляет '-sb-cnt'.
    // Точно воспроизводим исходное поведение.
    if (linkId.includes('cnt-menu')) {
      result = linkId.replace('cnt-menu', 'link')
      result = result + '-sb-cnt'
    } else {
      result = linkId + '-sb-cnt'
    }
    return result
  }

  /**
   * Вспомогательный метод для добавления или удаления класса 'active' из списка элементов.
   * @param {Array<Element|null>} elementList - Список элементов для изменения.
   * @param {boolean} isActivate - True для добавления 'active', false для удаления.
   * @private
   */
  static _toggleActiveClass(elementList, isActivate) {
    elementList.forEach(element => {
      if (element && element.classList) {
        if (isActivate) {
          element.classList.add('active')
        } else {
          element.classList.remove('active')
        }
      }
    })
  }
}

// Инициализируем функциональность сайдбара и привязываем слушатели, когда DOM полностью загружен
document.addEventListener('DOMContentLoaded', () => {
  // Инициализируем элементы во всех сервисах
  SidebarService.init()
  SideBarLinkService.init()
  CntCaptionLinkService.init()

  // Привязываем слушатели во всех сервисах
  SidebarService.attachListeners()
  SideBarLinkService.attachListeners()
  CntCaptionLinkService.attachListeners()

  /**
   * Примечание: Если нужно восстановить ранее активное состояние при загрузке страницы,
   * потребуется сохранить ID активных элементов в localStorage при их активации
   * и реализовать логику восстановления в CntCaptionLinkService.init или отдельном методе.
   * Текущая реализация хранит активное состояние только в памяти (статических свойствах),
   * поэтому оно сбрасывается при перезагрузке страницы.
   */
})
