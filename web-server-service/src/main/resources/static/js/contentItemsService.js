import { loadTextFileAsElement } from './initFragments.js'
import { handleMapNavigation, mapServices } from './maps/map.js'

//Лисенеры клавиатуры
document.addEventListener('keydown', e => {
  //console.log(`Нажата клавиша ${e.key}`)

  handleMapNavigation(e, mapServices)
})

import { RoutesMapService, OneRouteMapService } from './maps/map.js'

/*
const contentData = {
  cntId: 'cher-chuches-route',
  cntTitle: 'Маршруты по церквям Чернигова',
  columns: { titles: columnNames, data: rowData },
  cntType: 'default'
}
*/

class ContentService {
  contentWrapper
  contentItemsServices = {}
  activeContentItem
  prevActiveContentItem

  constructor() {
    this.contentWrapper = document.querySelector('.cabinet-content')
    this.init()
  }

  //Блок Инициализации
  async init() {
    const contentItemServices = {}
    const cntItems = document
      .querySelector('.cabinet-content')
      .querySelectorAll('.sb-cnt')

    //Инициализация сервисов контента
    for (const cntItem of cntItems) {
      const cntId = cntItem.getAttribute('id')

      const cntService = new ContentItemsService()

      await cntService.init(cntId)

      contentItemServices[cntId] = cntService
    }

    this.contentItemsServices = contentItemServices

    console.log('contentItemServices', this.contentItemsServices)
  }

  initContentItem(cntId) {
    const cntService = new ContentItemsService()
    cntService.init(cntId)
    this.contentItemsServices[contentData.cntId] = cntService
  }

  async createContentItem(contentData) {
    const cntService = new ContentItemsService()
    await cntService.createContentItems(contentData)
    this.contentItemsServices[contentData.cntId] = cntService

    document
      .querySelector('.cabinet-content')
      .appendChild(cntService.cntContainer)
  }

  isContentItemExist(cntId) {
    if (this.contentItemsServices[cntId]) return true
  }

  //Блок Геттеров
  getContentItemsService(cntId) {
    return this.contentItemsServices[cntId]
  }

  getActiveContentItem() {
    return this.activeContentItem
  }

  getPrevActiveContentItem() {
    return this.prevActiveContentItem
  }

  //Основной блок
  isWrapperActive() {
    if (this.contentWrapper.classList.contains('show')) return true
  }

  activateWrapper() {
    this.contentWrapper.classList.add('show')

    if (this.isWrapperCollapsed()) {
      this.collapsedWrapper()
    } else {
      this.expandWrapper()
    }
  }

  deactiveWrapper() {
    this.contentWrapper.classList.remove('show')
  }

  collapsedWrapper() {
    this.contentWrapper.classList.remove('hidden-sidebar')
  }

  expandWrapper() {
    this.contentWrapper.classList.add('hidden-sidebar')
  }

  isWrapperCollapsed() {
    const sidebar = document.querySelector('.sidebar')
    if (sidebar.classList.contains('open')) return true
  }

  activateContentItem(cntId) {
    if (!this.isWrapperActive()) {
      this.activateWrapper()
    }

    this.prevActiveContentItem = this.activeContentItem
    this.activeContentItem = this.contentItemsServices[cntId]

    if (this.prevActiveContentItem) {
      this.prevActiveContentItem.cntContainer.classList.remove('active')
    }

    this.activeContentItem.cntContainer.classList.add('active')
  }

  returnToPrevActiveContentItem() {
    const activeContentItem = this.activeContentItem.cntContainer
    activeContentItem.classList.remove('active')
    this.activeContentItem = this.prevActiveContentItem
    this.activeContentItem.cntContainer.classList.add('active')
    this.prevActiveContentItem = activeContentItem
  }

  deactiveContentItems() {
    const activeContentItem = this.activeContentItem.cntContainer
    activeContentItem.classList.remove('active')
    this.activeContentItem = null
    this.prevActiveContentItem = null
  }

  toggleActiveContentFormat() {
    this.activeContentItem.toggleActiveContentFormat()
  }
}

class ContentItemsService {
  cntContainer
  cntType
  tblService
  titleService
  activeContentItem

  constructor() {
    this.tblService = new TableSevice()
    this.titleService = new TitleSevice()
  }

  async createContentItems(contentData) {
    const cntId = contentData.cntId
    const cntIdForItems = cntId.replace('link-', '').replace('-sb-cnt', '')
    const cntTitleText = contentData.cntTitle
    const columns = contentData.columns
    const cntType = contentData.cntType
    this.cntType = cntType

    const cnt = document.createElement('div')
    const prevIcon = this.getReturnPrevPageIconHtmlElement()
    const cntTitleContainer = document.createElement('div')
    const cntTitle = document.createElement('h3')
    const cntCaption = await this.getContentHeaderContainerHemlElement(
      contentData
    )

    await this.tblService.createTable(`${cntIdForItems}-tbl`, columns, cntType)

    await this.titleService.createTitle(
      `${cntIdForItems}-title`,
      columns,
      cntType
    )

    cnt.classList.add('sb-cnt')
    cnt.id = cntId

    cntTitleContainer.classList.add('sb-cnt-title')

    cntTitle.innerHTML = cntTitleText

    cntTitleContainer.appendChild(cntTitle)

    cnt.appendChild(prevIcon)
    cnt.appendChild(cntTitleContainer)
    cnt.appendChild(cntCaption)
    cnt.appendChild(this.tblService.itemContainer)
    cnt.appendChild(this.titleService.item)

    this.setActiveContentItem()

    this.cntContainer = cnt

    console.log('cnt: ', cnt)

    await this.createListeners()
  }

  async init(cntId) {
    const cntContainer = document.getElementById(cntId)
    const cntType = cntContainer.getAttribute('data-cnt-type')

    if (cntType) {
      this.cntType = cntType
    }

    if (cntContainer) {
      this.cntContainer = cntContainer

      let cntIdForItems = cntId.replace('link-', '')
      cntIdForItems = cntIdForItems.replace('-sb-cnt', '')

      this.tblService.initTable(`${cntIdForItems}-tbl`)
      this.titleService.initTitle(`${cntIdForItems}-title`)

      if (this.tblService.isInit()) {
        this.setActiveContentItem()
      }

      await this.createListeners()
    }
  }

  async createListeners() {
    const self = this

    this.cntContainer.addEventListener('click', async e => {
      const target = e.target

      //console.log('target: ', target.classList)

      //Переключения видимости контента при клике на ссылку меню в шапке контейнера контента
      if (target.classList.contains('cnt-menu-link-title')) {
        const link = target

        toggleActiveMenuLinks(link, true)
      }

      //Иконка возвращения к общей таблице меню контента
      if (target.classList.contains('cnt-return-prev-page-icon')) {
        contentService.returnToPrevActiveContentItem()
      }

      //Переключения формата отображения контента
      if (target.classList.contains('cnt-tab')) {
        const cntItem = target.closest('.sb-cnt')
        const cntTabsContainer = target.closest('.cnt-tabs')
        const cntTab = target

        if (!cntTab.classList.contains('active')) {
          for (const tab of cntTabsContainer.children) {
            if (tab.classList.contains('active')) {
              tab.classList.remove('active')
            }
          }
          cntTab.classList.add('active')
        }
        self.toggleActiveContentFormat()
      }

      //Переключения между способами сортировки контента
      if (target.classList.contains('sb-sorting-link')) {
        const selectedSortingLink = target
        const cntContainer = selectedSortingLink.closest('.sb-cnt')
        const sortingLinksConainer = selectedSortingLink.closest(
          '.sb-sorting-links-container'
        )
        const selectEl = sortingLinksConainer
          .closest('.sb-sorting-links-controls')
          .querySelector('.sb-sorting-links-select')
        const sortingLinks =
          sortingLinksConainer.querySelectorAll('.sb-sorting-link')

        //Если колонка для сортировки не выбрана включаем окно предупреждения
        if (selectEl.selectedIndex != 0) {
          //Убираем активное состояние у остальных ссылок
          if (!selectedSortingLink.classList.contains('active')) {
            for (const link of sortingLinks) {
              if (link != selectedSortingLink) {
                link.classList.remove('active')
              }
            }
            selectedSortingLink.classList.add('active')
          }
        } else {
          self.toggleVisibilitySelectSortingWarningWindow()
        }
      }

      //Функция изменения иконки "избранное"
      if (target.classList.contains('bx-star')) {
        target.classList.remove('bx-star')
        target.classList.add('bxs-star', 'active')
        target.title = 'Удалить из избранного'
      } else if (target.classList.contains('bxs-star')) {
        target.classList.remove('bxs-star', 'active')
        target.classList.add('bx-star')
        target.title = 'Добавить в избранное'
      }

      //Функция показать маршрута на карте
      if (target.classList.contains('route-map-pin')) {
        const mapContainer = target
          .closest('.sb-cnt')
          .querySelector('.content-map-container')

        const mapService = self.getMapServiceByContainerId(mapContainer.id)

        mapContainer.scrollIntoView({ behavior: 'smooth', block: 'center' })

        mapService.selectRoute(target.getAttribute('route-id'))

        mapService.fitMapToSelectedRoute()
      }

      //Функция показа маркера на карте
      if (target.classList.contains('marker-pin-icon')) {
        const mapContainer = target
          .closest('.sb-cnt')
          .querySelector('.content-map-container')

        const mapService = self.getMapServiceByContainerId(mapContainer.id)

        mapContainer.scrollIntoView({ behavior: 'smooth', block: 'center' })

        mapService.selectMarkerById(target.getAttribute('marker-id'))

        mapService.fitMapToSelectedMarker()
      }

      //Функция отрытия записи контента
      if (target.classList.contains('cnt-open-rec-icon')) {
        const parentCntId = target.closest('.sb-cnt').id
        const parentCntItemService =
          contentService.getContentItemsService(parentCntId)

        console.log('parentCntItemService: ', parentCntItemService)

        if (parentCntItemService.cntType === 'map') {
          const routeId = self.getRouteIdByActionElement(target)
          const cntId = `link-${routeId}-sb-cnt`

          if (!contentService.isContentItemExist(cntId)) {
            const mapService = self.getMapServiceByRouteId(routeId)
            const routeData = mapService.getRouteData(routeId)

            const columnNames = ['Название точки', 'Адрес', 'Ключ', 'Описание']
            const rowData = []
            const markersId = []

            routeData.points.forEach(point => {
              const row = []

              row.push(point.name)
              row.push(point.address)
              row.push(point.key)
              row.push(point.description)

              rowData.push(row)
              markersId.push(point.id)
            })

            const contentData = {
              cntId: `link-${routeId}-sb-cnt`,
              cntTitle: routeData.routeName,
              columns: {
                titles: columnNames,
                data: rowData,
                mapItemsId: markersId
              },
              cntType: 'map'
            }

            await contentService.createContentItem(contentData)

            let currentRouteMap
            const currentRoute = mapService.getRoute(routeId)

            contentService.activateContentItem(contentData.cntId)

            if (!self.hasLeafletMap(routeId)) {
              currentRouteMap = new OneRouteMapService()

              mapServices.push(currentRouteMap)

              currentRouteMap.create(`${routeId}-map`, currentRoute)
            }
          } else {
            contentService.activateContentItem(cntId)
          }
        }
      }

      //Функция изменения иконки "показать/скрыть" запись куратору
      if (target.classList.contains('bx-show')) {
        target.classList.remove('bx-show')
        target.classList.add('bx-hide')
        target.title = 'Сделать запись доступной куратору'
      } else if (target.classList.contains('bx-hide')) {
        target.classList.add('bx-show')
        target.classList.remove('bx-hide')
        target.title = 'Сделать запись не доступной куратору'
      }

      //Вызов модального окна при нажатии на иконку редактирования записи контента
      if (target.classList.contains('cnt-edit-icon')) {
        if (target.classList.contains('title-edit-icon')) {
          self.showModalWillEditInformation(
            target.closest('.sb-cnt-title-container-item')
          )
        } else {
          self.showModalWillEditInformation(target.closest('tr'))
        }
      }

      //Функция удаления записи контента
      if (target.classList.contains('cnt-dlt-icon')) {
        let cell = self.activeContentItem.getItemDataRowByElement(target)

        if (cell) {
          cell.remove()
        } else {
          console.log('Cell not found')
        }
      }
    })
  }

  isInit() {
    if (this.activeContentItem) {
      return true
    } else {
      return false
    }
  }

  setActiveContentItem() {
    if (this.tblService.isActive()) {
      this.activeContentItem = this.tblService
    } else {
      this.activeContentItem = this.titleService
    }
  }

  toggleActiveContentFormat() {
    const self = this
    const tabs = this.getContentTabsHtmlElements()
    const tblTab = tabs[0]
    const titleTab = tabs[1]

    self.activeContentItem.deactivate()

    if (self.activeContentItem.item.tagName.toLowerCase() === 'table') {
      tblTab.classList.remove('active')
      titleTab.classList.add('active')
      self.activeContentItem = self.titleService
    } else {
      tblTab.classList.add('active')
      titleTab.classList.remove('active')
      self.activeContentItem = self.tblService
    }

    self.activeContentItem.activate()
  }

  isTable(element) {
    const contentContainer = element.closest('.sb-cnt-item')
    if (contentContainer.classList.contains('sb-cnt-tbl-container')) {
      return true
    } else {
      return false
    }
  }

  isActive() {
    return this.cntContainer.classList.contains('active')
  }

  selectItemDataRow(element) {
    this.activeContentItem.selectRow(element)
  }

  deselectItemDataRow() {
    if (this.activeContentItem.selectedItem !== null)
      this.activeContentItem.deselectRow()
  }

  toggleSelectedContentItemDataRow(elemet) {
    if (this.activeContentItem.selectedItem !== null) {
      this.activeContentItem.deselectRow()
    } else {
      this.activeContentItem.selectRow(elemet)
    }
  }

  toggleVisibilitySelectSortingWarningWindow() {
    const cntContainer = this.cntContainer
    const warningWindow = cntContainer
      .querySelector('.sb-sorting-container')
      .querySelector('.cnt-search-wrn')

    if (!warningWindow.classList.contains('active')) {
      warningWindow.classList.add('active')
    } else {
      warningWindow.classList.remove('active')
    }
  }

  hasLeafletMap(containerId) {
    const el = document.getElementById(containerId)
    return !!(el && el._leaflet_id) // true, если карта уже есть
  }

  getMapServiceByContainerId(containerId) {
    return mapServices.find(
      mapService => mapService.getContainerId() == containerId
    )
  }

  getMapServiceByRouteId(routeId) {
    console.log('getMapServiceByRouteId')
    console.log('routeId: ', routeId)
    return mapServices.find(mapService => {
      if (mapService.getRoute(routeId).routedata.routeId == routeId) {
        console.log('mapService: ', mapService)
        return mapService
      } else {
        return null
      }
    })
  }

  getRouteIdByActionElement(element) {
    return element
      .closest('.actions')
      .querySelector('.route-map-pin')
      .getAttribute('route-id')
  }

  showModalWillEditInformation(row) {
    const self = this
    let cntId

    if (row.tagName == 'TR') {
      cntId = row.closest('table').id
    } else {
      cntId = row.closest('.sb-cnt-title-container').id
    }
    const modal = document.getElementById('cnt-item-modal-container')
    const form = modal.querySelector('form')

    form.setAttribute('obj-id', row.getAttribute('obj-id'))
    form.setAttribute('cnt-id', cntId)

    modal.classList.add('show')

    // Установка значений полей в модальном окне из текущей записи
    const willIncomingDate = self.convertDateForEditForm(
      row.querySelector('.will-incoming-date').textContent.trim()
    )

    const willDescription = row
      .querySelector('.will-brief-description')
      .textContent.trim()
    const willComment = row.querySelector('.will-comment').textContent.trim()

    document.getElementById('edit-will-incoming-date').value = willIncomingDate
    document.getElementById('edit-will-brief-description').value =
      willDescription
    document.getElementById('edit-will-comment').value = willComment
  }

  //Функция конвертации даты с формата дд.мм.гггг в формат гггг-мм-дд
  convertDateForEditForm(date) {
    const [day, month, year] = date.split('.')
    return `${year}-${month}-${day}`
  }

  //Html элементы
  getContentContainerHtmlElement() {
    return this.cntContainer
  }

  getReturnPrevPageIconHtmlElement() {
    const returnPrevPageIcon = document.createElement('i')
    returnPrevPageIcon.classList.add(
      'bx',
      'bx-arrow-back',
      'cnt-return-prev-page-icon'
    )
    return returnPrevPageIcon
  }

  getContentTabsHtmlElements() {
    return this.cntContainer
      .querySelector('.cnt-tabs')
      .querySelectorAll('.cnt-tab')
  }

  async getContentHeaderContainerHemlElement(contentData) {
    const cntId = contentData.cntId
    const cntType = this.cntType

    if (cntType === 'map') {
      let idForMap = cntId.replace('link-', '')
      idForMap = idForMap.replace('-sb-cnt', '')

      const contentHeaderContainer =
        await this.loadContentHeaderContainerHtmlElement()

      contentHeaderContainer.classList.add(`${cntId}-header`)

      contentHeaderContainer
        .querySelector('.content-map-wrapper')
        .classList.add(`${idForMap}-map-container`)

      contentHeaderContainer.querySelector(
        '.content-map-container'
      ).id = `${idForMap}-map`

      return contentHeaderContainer
    } else {
      return await this.loadContentTabsContainerHtmlElement()
    }
  }

  async loadContentTabsContainerHtmlElement() {
    const contentTabsContainer = await loadTextFileAsElement(
      './partials/sidebarContent/contentItemsElements/tabsContainer.txt'
    )
    return contentTabsContainer
  }

  async loadContentHeaderContainerHtmlElement() {
    const contentHeaderContainer = await loadTextFileAsElement(
      './partials/sidebarContent/contentItemsElements/contentHeader.txt'
    )
    return contentHeaderContainer
  }

  async loadContentSortingContainerHtmlElement() {
    const contentSortingContainer = await loadTextFileAsElement(
      './partials/sidebarContent/contentItemsElements/sortingContainer.txt'
    )
    return contentSortingContainer
  }
}

class TableSevice {
  item
  itemContainer
  selectedItem = null

  //Блок инициализации
  initTable(tblId) {
    const tbl = document.getElementById(tblId)

    if (tbl) {
      tbl.classList.add('active')
      this.item = tbl
      this.itemContainer = tbl.closest('.sb-cnt-item')
    }
  }

  async createTable(tblId, columns, cntType) {
    const tblContainer = document.createElement('div')

    const caption = await this.getTableCaption(
      './partials/sidebarContent/contentItemsElements/tableCaption.txt'
    )
    const footer = await this.getTableFooter(
      './partials/sidebarContent/contentItemsElements/tableFooter.txt'
    )

    const tbl = document.createElement('table')
    const tblHeader = document.createElement('thead')
    const tblBody = document.createElement('tbody')

    tblContainer.classList.add('sb-cnt-item', 'sb-cnt-tbl-container', 'active')

    tblContainer.appendChild(caption)
    tblContainer.appendChild(tbl)

    tbl.classList.add('cnt-tbl')
    tbl.setAttribute('id', tblId)

    this.populateTableHeader(tblHeader, columns.titles)
    this.populateTableBody(tblId, tblBody, columns, cntType)

    tbl.appendChild(tblHeader)
    tbl.appendChild(tblBody)

    tblContainer.appendChild(tbl)

    tblContainer.appendChild(footer)

    this.itemContainer = tblContainer
    this.item = tbl
    this.item.id = tblId
  }

  isInit() {
    if (this.item) {
      return true
    } else {
      return false
    }
  }

  //Основной блок
  activate() {
    this.itemContainer.classList.add('active')
  }

  deactivate() {
    this.itemContainer.classList.remove('active')
  }

  getItemDataRowByElement(element) {
    return element.closest('tr')
  }

  //Наполнение данными таблицы
  populateTableHeader(tblHeader, columnNames) {
    const tblHeaderRow = document.createElement('tr')

    //Добавляем основные колонки
    columnNames.forEach(name => {
      const tblHeaderCell = document.createElement('th')
      const tblHeaderLink = document.createElement('a')
      const tblHeaderLinkChevron = document.createElement('span')

      tblHeaderLinkChevron.classList.add('fas', 'fa-caret-down')

      tblHeaderLink.classList.add('tbl-title-link')
      tblHeaderLink.href = '#'
      tblHeaderLink.textContent = name
      tblHeaderLink.appendChild(tblHeaderLinkChevron)
      tblHeaderCell.appendChild(tblHeaderLink)

      tblHeaderCell.classList.add('tbl-title', 'tbl-data-title')
      tblHeaderRow.appendChild(tblHeaderCell)
      tblHeader.appendChild(tblHeaderRow)
    })

    //Добавляем колонку для функциональных иконок и мультиселекта
    const tblHeaderActionCell = document.createElement('th')
    const tblHeaderMiltiselectCell = document.createElement('th')

    tblHeaderActionCell.classList.add('tbl-title', 'tbl-actions-title')
    tblHeaderRow.appendChild(tblHeaderActionCell)

    tblHeaderMiltiselectCell.classList.add('tbl-title', 'tbl-multiselect-title')
    tblHeaderRow.appendChild(tblHeaderMiltiselectCell)

    tblHeader.appendChild(tblHeaderRow)
  }

  populateTableBody(tblId, tblBody, columns, cntType) {
    const data = columns.data
    const mapItemsId = columns.mapItemsId
    const routeId = tblId.replace('-tbl', '')

    data.forEach((row, index) => {
      const tblBodyRow = document.createElement('tr')
      tblBodyRow.classList.add('tbl-body-row')
      tblBodyRow.setAttribute('obj-id', index)

      row.forEach(cell => {
        const tblBodyCell = document.createElement('td')
        tblBodyCell.classList.add('tbl-row-data')

        tblBodyCell.textContent = cell
        tblBodyRow.appendChild(tblBodyCell)
      })

      tblBodyRow.appendChild(
        this.getTableBodyActionsHtmlContainer(
          routeId,
          mapItemsId[index],
          cntType
        )
      )

      tblBodyRow.appendChild(this.getTableBodySelectHtmlContainer())

      tblBody.appendChild(tblBodyRow)
    })
  }

  getTableBodyActionsHtmlContainer(routeId, mapItemId, cntType) {
    console.log('cntType', cntType)
    const actionsContainer = document.createElement('td')
    const favIcon = document.createElement('i')
    const openCellDataIcon = document.createElement('i')
    const editCellDataIcon = document.createElement('i')
    const deleteCellDataIcon = document.createElement('i')
    const showCellCuratorIcon = document.createElement('i')

    actionsContainer.classList.add('actions')

    favIcon.classList.add('bx', 'bx-star', 'favicon')
    favIcon.setAttribute('title', 'Добавить в избранное')

    openCellDataIcon.classList.add(
      'bx',
      'bx-link-external',
      'cnt-open-rec-icon',
      'tbl-open-rec-icon'
    )
    openCellDataIcon.setAttribute('title', 'Открыть запись')

    editCellDataIcon.classList.add(
      'bx',
      'bxs-pencil',
      'cnt-edit-icon',
      'tbl-edit-icon'
    )
    editCellDataIcon.setAttribute('title', 'Отредактировать запись')

    deleteCellDataIcon.classList.add(
      'bx',
      'bxs-trash-alt',
      'cnt-dlt-icon',
      'tbl-dlt-icon'
    )
    deleteCellDataIcon.setAttribute('title', 'Удалить запись')

    showCellCuratorIcon.classList.add('bx', 'bx-show', 'curator-access-icon')
    showCellCuratorIcon.setAttribute(
      'title',
      'Сделать запись не доступной куратору'
    )

    actionsContainer.appendChild(favIcon)

    if (cntType === 'map') {
      const mapPinIcon = document.createElement('i')

      mapPinIcon.classList.add('bx', 'bx-map-pin', 'marker-pin-icon')
      mapPinIcon.classList.add('bx', 'bx-map-pin')
      mapPinIcon.setAttribute('marker-id', mapItemId)
      mapPinIcon.setAttribute('route-id', routeId)
      mapPinIcon.setAttribute('title', 'Показать на карте')
      actionsContainer.appendChild(mapPinIcon)
    }

    actionsContainer.appendChild(openCellDataIcon)
    actionsContainer.appendChild(editCellDataIcon)
    actionsContainer.appendChild(deleteCellDataIcon)
    actionsContainer.appendChild(showCellCuratorIcon)

    return actionsContainer
  }

  getTableBodySelectHtmlContainer() {
    const container = document.createElement('td')
    const lable = document.createElement('label')
    const input = document.createElement('input')
    const span = document.createElement('span')

    lable.classList.add('cnt-lb-checkbox', 'tbl-lb-checkbox')

    input.classList.add('cnt-checkbox', 'tbl-checkbox')
    input.setAttribute('type', 'checkbox')

    span.classList.add('cnt-checkmark', 'tbl-checkmark')

    lable.appendChild(input)
    lable.appendChild(span)

    container.appendChild(lable)

    return container
  }

  async getTableCaption(path) {
    const caption = await loadTextFileAsElement(path)
    return caption
  }

  async getTableFooter(path) {
    const footer = await loadTextFileAsElement(path)
    return footer
  }

  getColumnTitles() {
    const columnTitles = []
    const tblHeaders = this.item.querySelectorAll('.tbl-title')
    tblHeaders.forEach(header => {
      columnTitles.push(header.textContent)
    })
    return columnTitles
  }

  selectRow(element) {
    const row = element.closest('.tbl-body-row')
    row.classList.toggle('selected')
    this.selectedItem = row
  }

  deselectRow() {
    if (this.selectedItem === null) return
    this.selectedItem.classList.remove('selected')
    this.selectedItem = null
  }

  isActive() {
    const tbl = this.item
    const tblContainer = tbl.closest('.sb-cnt-item')

    if (tblContainer.classList.contains('active')) {
      return true
    } else {
      return false
    }
  }
}

class TitleSevice {
  itemContainer
  item
  selectedItem = null

  //Блок инициализации
  initTitle(titleId) {
    const title = document.getElementById(titleId)

    if (title) {
      this.item = title
      this.itemContainer = title
    }
  }

  isInit() {
    if (this.item) {
      return true
    } else {
      return false
    }
  }

  async createTitle(titleId, columns, cntType) {
    const title = document.createElement('div')

    const caption = await this.getTitleCaption(
      './partials/sidebarContent/contentItemsElements/titleCaption.txt'
    )
    const footer = await this.getTitleFooter(
      './partials/sidebarContent/contentItemsElements/titleFooter.txt'
    )

    title.classList.add('sb-cnt-item', 'sb-cnt-title-container')
    title.setAttribute('id', titleId)

    title.appendChild(caption)

    this.populateTitle(title, columns, cntType)

    title.appendChild(footer)

    this.item = title
    this.item.id = titleId

    return title
  }

  //Основной блок
  activate() {
    this.item.classList.add('active')
  }

  deactivate() {
    this.item.classList.remove('active')
  }

  selectRow(element) {
    const titleItem = element.closest('.sb-cnt-title-container-item')
    titleItem.classList.toggle('selected')
    this.selectedItem = titleItem
  }

  deselectTitleItem() {
    if (this.selectedItem === null) return
    this.selectedItem.classList.remove('selected')
    this.selectedItem = null
  }

  isActive() {
    if (this.item.classList.contains('active')) {
      return true
    } else {
      return false
    }
  }

  getItemDataRowByElement(element) {
    return element.closest('.sb-cnt-title-container-item')
  }

  //Заполнение плитки данными
  populateTitle(title, columns, cntType) {
    const self = this

    const titleCells = self.getTitleCellsNamesHtmlElements(columns.titles)
    const titleData = self.getTitleCellsDataHtmlElements(columns.data)

    titleData.forEach((data, index) => {
      const titleItem = document.createElement('div')
      const multiselectLable = self.getTitleSelectHtmlContainer()
      const favIcon = self.getFavIcon()
      const titleActions = self.getTitleActionsHtmlContainer(cntType)

      titleItem.classList.add('sb-cnt-title-container-item')
      titleItem.setAttribute('obj-id', index)

      //Добавляем чекбокс мультиселекта
      titleItem.appendChild(multiselectLable)
      titleItem.appendChild(favIcon)

      //Добавляем основные колонки

      for (let i = 0; i < data.length; i++) {
        const titleCellContainer = document.createElement('div')

        titleCellContainer.classList.add('sb-cnt-title-container-el')

        titleCellContainer.appendChild(titleCells[i].cloneNode(true))
        titleCellContainer.appendChild(data[i])

        titleItem.appendChild(titleCellContainer)
      }

      //Добавляем функциональные иконки
      titleItem.appendChild(titleActions)
      title.appendChild(titleItem)
    })
  }

  getTitleSelectHtmlContainer() {
    const lable = document.createElement('label')
    const input = document.createElement('input')
    const span = document.createElement('span')

    lable.classList.add('cnt-lb-checkbox', 'tbl-lb-checkbox')

    input.classList.add('cnt-checkbox', 'tbl-checkbox')
    input.setAttribute('type', 'checkbox')

    span.classList.add('cnt-checkmark', 'tbl-checkmark')

    lable.appendChild(input)
    lable.appendChild(span)

    return lable
  }

  getFavIcon() {
    const favIcon = document.createElement('i')

    favIcon.classList.add('bx', 'bx-star', 'favicon')
    favIcon.setAttribute('title', 'Добавить в избранное')

    return favIcon
  }

  getTitleCellsNamesHtmlElements(names) {
    const result = []

    names.forEach(name => {
      const cellName = document.createElement('h4')

      cellName.textContent = name

      result.push(cellName)
    })

    return result
  }

  getTitleCellsDataHtmlElements(data) {
    const result = []

    data.forEach(items => {
      const resultItem = []

      items.forEach(item => {
        const cellData = document.createElement('p')
        cellData.classList.add('title-data')

        cellData.textContent = item

        resultItem.push(cellData)
      })

      result.push(resultItem)
    })
    return result
  }

  getTitleActionsHtmlContainer(cntType) {
    const actionsContainer = document.createElement('div')
    const openCellDataIcon = document.createElement('i')
    const editCellDataIcon = document.createElement('i')
    const deleteCellDataIcon = document.createElement('i')
    const showCellCuratorIcon = document.createElement('i')

    actionsContainer.classList.add('actions')

    openCellDataIcon.classList.add(
      'bx',
      'bx-link-external',
      'cnt-open-rec-icon',
      'tbl-open-rec-icon'
    )
    openCellDataIcon.setAttribute('title', 'Открыть запись')

    editCellDataIcon.classList.add(
      'bx',
      'bxs-pencil',
      'cnt-edit-icon',
      'tbl-edit-icon'
    )
    editCellDataIcon.setAttribute('title', 'Отредактировать запись')

    deleteCellDataIcon.classList.add(
      'bx',
      'bxs-trash-alt',
      'cnt-dlt-icon',
      'tbl-dlt-icon'
    )
    deleteCellDataIcon.setAttribute('title', 'Удалить запись')

    showCellCuratorIcon.classList.add('bx', 'bx-show', 'curator-access-icon')
    showCellCuratorIcon.setAttribute(
      'title',
      'Сделать запись не доступной куратору'
    )

    if (cntType === 'map') {
      const mapPinIcon = document.createElement('i')

      mapPinIcon.classList.add('bx', 'bx-map-pin')
      mapPinIcon.setAttribute('title', 'Показать на карте')
      actionsContainer.appendChild(mapPinIcon)
    }

    actionsContainer.appendChild(openCellDataIcon)
    actionsContainer.appendChild(editCellDataIcon)
    actionsContainer.appendChild(deleteCellDataIcon)
    actionsContainer.appendChild(showCellCuratorIcon)

    return actionsContainer
  }

  async getTitleCaption(path) {
    const caption = await loadTextFileAsElement(path)
    return caption
  }

  async getTitleFooter(path) {
    const footer = await loadTextFileAsElement(path)
    return footer
  }

  getColumnTitles() {
    const columnTitles = []
    const tblHeaders = this.tbl.querySelectorAll('.tbl-title')
    tblHeaders.forEach(header => {
      columnTitles.push(header.textContent)
    })
    return columnTitles
  }
}

export const contentService = new ContentService()
