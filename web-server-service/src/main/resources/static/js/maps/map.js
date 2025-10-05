import L, { extend, map, marker } from 'leaflet'
//import 'leaflet/dist/leaflet.css'
import { contentService } from '../contentItemsService.js'
import { Modal, ModalImage } from '../modal.js'
import { MapConstans } from './mapConstans.js'

export const mapServices = new Array()

class BaseMapService {
  createMap(mapContainerId) {
    this.map = L.map(mapContainerId, {
      zoomControl: false,
      attributionControl: false,
      keyboard: false
    }).setView([51.487889, 31.303815], 13) // Чернигов

    const openStreetMap = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
    )

    openStreetMap.addTo(this.map)

    // Контроль переключения слоев
    const baseLayers = {
      Карта: openStreetMap,
      Спутник: L.tileLayer(
        'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}'
      )
    }

    // Добавляем zoom-контрол в нужное место
    L.control
      .zoom({
        position: 'topright' // например, правый верхний угол внутри карты
      })
      .addTo(this.map)

    L.control
      .layers(baseLayers, null, {
        position: 'topright',
        collapsed: true
      })
      .addTo(this.map)
  }

  populateMap() {
    //console.log('Метод populateMap')
    const map = this.map
    //console.log('map: ', map)
    const routes = this.routesService.routes
    const markersService = this.markersService
    const routeMarkers = markersService.routeMarkers

    //console.log('routeMarkers: ', routeMarkers)

    //Добавление маршрутов
    for (let route in routes) {
      const shape = routes[route].shape
      shape.addTo(map)
    }

    //Добавление маркеров
    for (let routeId in routeMarkers) {
      const markers = routeMarkers[routeId]
      console.log('markers: ', markers)
      markers.forEach(marker => marker.addTo(map))
    }

    this.fitMapToVisibleRoutes()
  }

  async createMapControls() {
    const self = this
    const routesServices = this.routesService
    const markerStyles = this.markersService.markerStyles
    const moskowSvg = await this.getMoskowSvgElement()
    const tverSvg = await this.getTverSvgElement()

    switch (this.mapType) {
      case 'routes-map':
        const RouteToggleControl = L.Control.extend({
          onAdd: function (map) {
            const container = L.DomUtil.create(
              'div',
              'leaflet-bar leaflet-control leaflet-route-control collapsed'
            )

            //Шапка контейнера
            const titleContainer = document.createElement('div')
            const title = document.createElement('div')
            const chevronIcon = document.createElement('i')
            titleContainer.className =
              'leaflet-control-title-container leaflet-route-control-title-container'
            title.className =
              'leaflet-control-title leaflet-route-control-title'
            title.innerText = 'Маршруты:'

            chevronIcon.className = 'bx control-title-chevron bx-chevron-down'

            titleContainer.appendChild(title)
            titleContainer.appendChild(chevronIcon)
            container.appendChild(titleContainer)

            //Добавление списка маршрутов
            const routesContainer = document.createElement('div')
            routesContainer.className = 'leaflet-groups leaflet-route-groups'
            // Группировка маршрутов по названию города
            const groupedRoutes = routesServices.groupeRoutesByCity()

            // Создание элементов интерфейса для каждой группы (города)
            for (const [city, cityRoutes] of Object.entries(groupedRoutes)) {
              const cityContainer = document.createElement('div')

              cityContainer.className = 'leaflet-group leaflet-route-group'

              const cityHeader = document.createElement('div')
              cityHeader.className =
                'leaflet-group-title leaflet-route-group-title'
              cityHeader.innerText = city

              cityContainer.appendChild(cityHeader)

              cityRoutes.forEach(({ routeId, route }) => {
                const routeData = route.routedata
                const label = document.createElement('label')
                const checkbox = document.createElement('input')
                const routeSymbol = document.createElement('div')
                const routeStyle = route.shapeStyles.defaultStyle
                const routeName = document.createElement('span')

                checkbox.type = 'checkbox'
                checkbox.checked = true
                checkbox.dataset.routeId = routeId

                //Лисенер показа/скрытия маршрута
                checkbox.addEventListener('change', function () {
                  const routeId = this.dataset.routeId
                  const route = routesServices.routes[routeId]

                  if (this.checked) {
                    self.showOrHideRoute(routeId)
                  } else {
                    self.showOrHideRoute(routeId)
                  }
                })

                label.className = 'leaflet-route-label'
                label.appendChild(checkbox)

                routeSymbol.classList.add('leaflet-route-symbol')
                routeSymbol.style.borderTop = `${routeStyle.weight}px ${
                  routeStyle ? 'dashed' : 'solid'
                } ${routeStyle.color}`
                routeSymbol.style.opacity = routeStyle.opacity

                routeName.textContent = ' ' + routeData.routeName

                label.appendChild(routeSymbol)
                label.appendChild(routeName)
                cityContainer.appendChild(label)
              })
              routesContainer.appendChild(cityContainer)
            }

            container.appendChild(routesContainer)
            L.DomEvent.disableClickPropagation(container)
            return container
          }
        })
        this.map.addControl(
          new RouteToggleControl({ position: 'topright', collapsed: true })
        )
      case 'one-route-map':
        const MapSymbolsControl = L.Control.extend({
          onAdd: function (map) {
            const container = L.DomUtil.create(
              'div',
              'leaflet-bar leaflet-control leaflet-symbols-control collapsed'
            )

            //Шапка контейнера
            const titleContainer = document.createElement('div')
            const title = document.createElement('div')
            const chevronIcon = document.createElement('i')
            titleContainer.className =
              'leaflet-control-title-container leaflet-symbols-control-title-container'
            title.className =
              'leaflet-control-title leaflet-symbols-control-title'
            title.innerText = 'Условные обозначения:'

            chevronIcon.className = 'bx control-title-chevron bx-chevron-down'

            titleContainer.appendChild(title)
            titleContainer.appendChild(chevronIcon)
            container.appendChild(titleContainer)

            //Добавление списка маршрутов
            const symbolsContainer = document.createElement('div')
            symbolsContainer.className = 'leaflet-groups leaflet-symbols-groups'

            // Создание элементов интерфейса для каждой группы символов
            const symbolContainer = document.createElement('div')

            symbolContainer.className = 'leaflet-group leaflet-symbol-group'

            //Создание условных обозначений маркеров
            const symbolHeader = document.createElement('div')
            symbolHeader.className =
              'leaflet-group-title leaflet-symbol-group-title'
            symbolHeader.innerText = 'Маркеры точек'

            symbolContainer.appendChild(symbolHeader)

            Object.entries(markerStyles).forEach(([key, marker]) => {
              const style = marker.options
              const label = document.createElement('div')
              let markerSymbol = style.html
              markerSymbol = markerSymbol.replace(
                'icon-layer',
                'icon-layer no-hover'
              )

              const markerName = document.createElement('p')

              markerName.textContent = ' - ' + style.description

              label.className = 'leaflet-symbol-label'
              label.innerHTML = markerSymbol
              label.appendChild(markerName)

              symbolContainer.appendChild(label)
            })

            symbolsContainer.appendChild(symbolContainer)

            container.appendChild(symbolsContainer)
            L.DomEvent.disableClickPropagation(container)
            return container
          }
        })
        this.map.addControl(
          new MapSymbolsControl({ position: 'topleft', collapsed: true })
        )
      default:
        const MapContainerSizeControl = L.Control.extend({
          onAdd: function (map) {
            const container = L.DomUtil.create(
              'div',
              'leaflet-bar leaflet-control leaflet-container-fit-size-control'
            )

            if (self.mapType === 'one-route-map')
              container.classList.add('one-route-map')

            const controlSizeIcon = document.createElement('i')
            const controlFitSelectedRouteIcon = document.createElement('i')
            const controlFitMarkerIcon = document.createElement('i')

            controlFitMarkerIcon.className = 'fa-solid fa-map-location-dot'
            controlFitMarkerIcon.setAttribute(
              'title',
              'Показать выбранную точку'
            )
            controlFitSelectedRouteIcon.className = 'bx bx-shape-polygon'
            controlFitSelectedRouteIcon.setAttribute(
              'title',
              'Показать выделенный маршрут'
            )
            controlSizeIcon.className = 'bx bx-fullscreen'
            controlSizeIcon.setAttribute('id', 'map-fullscreen-control-icon')
            controlSizeIcon.setAttribute('title', 'Во весь экран')

            container.appendChild(controlFitMarkerIcon)

            if (self.mapType === 'routes-map') {
              container.appendChild(moskowSvg)
              container.appendChild(tverSvg)
            }

            container.appendChild(controlFitSelectedRouteIcon)
            container.appendChild(controlSizeIcon)

            L.DomEvent.disableClickPropagation(container)
            return container
          }
        })

        this.map.addControl(
          new MapContainerSizeControl({ position: 'bottomright' })
        )

        const MapContainerExternalMapsControl = L.Control.extend({
          onAdd: function (map) {
            const container = L.DomUtil.create(
              'div',
              'leaflet-bar leaflet-control leaflet-container-external-maps-control'
            )

            const gooleMapsIcon = document.createElement('i')
            const yandexMapIcon = document.createElement('i')

            gooleMapsIcon.className = 'fa-brands fa-google'
            gooleMapsIcon.setAttribute('title', 'Открыть в Google Maps')
            yandexMapIcon.className = 'fa-brands fa-yandex-international'
            yandexMapIcon.setAttribute('title', 'Открыть в Яндекс Карты')

            container.appendChild(gooleMapsIcon)
            container.appendChild(yandexMapIcon)

            L.DomEvent.disableClickPropagation(container)
            return container
          }
        })

        this.map.addControl(
          new MapContainerExternalMapsControl({
            position: 'bottomright'
          })
        )
        break
    }
  }

  //Лисенеры
  createMapListeners() {
    const self = this

    if (this.routesService) {
      console.log('Создание лисенеров')
      const mapContainer = self.map.getContainer()
      const routeService = self.routesService
      const routes = self.routesService.routes
      const markersService = self.markersService
      const routeMarkers = markersService.routeMarkers
      let isShapeClick = false
      const controlContainer = mapContainer.querySelector(
        '.leaflet-control-container'
      )
      const fullScreenWrapper = document.getElementById(
        'map-fullscreen-wrapper'
      )

      //Лисенер клика на карту
      this.map.on('click', e => {
        const currentSelectedMarker = this.markersService.currentMarker

        if (isShapeClick) {
          isShapeClick = false
          return
        }
        console.log('Клик по пустому месту карты')
        //console.log(`Координаты: ${e.latlng.lat}, ${e.latlng.lng}`)

        if (routeService.currentRoute != null) {
          self.deselectRoute()
        }

        if (currentSelectedMarker != null) self.deselectMarker()
      })

      //Лисенер клика на маршруты
      for (const [routeId, route] of Object.entries(routes)) {
        const shape = route.shape

        shape.on('click', e => {
          isShapeClick = true

          // 1. Предотвращаем дефолтное поведение
          e.originalEvent.preventDefault()

          // 2. Снимаем фокус
          const target = e.originalEvent.target
          target.blur()
          document.activeElement.blur()

          if (routeService.currentRoute !== route) {
            self.deselectRoute()
            self.selectRoute(routeId)
          } else {
            this.deselectRoute()
          }

          this.zoomToRoute(routeId)
        })
      }

      //Лисенеры для маркеров
      for (const [routeId, markers] of Object.entries(routeMarkers)) {
        markers.forEach(marker => {
          // Добавляем обработчики событий на маркер
          marker.on('click', function () {
            self.selectMarker(marker)
          })

          // Смена активности кнопок навигации между маркерами при открытии попапа
          marker.once('popupopen', () => {
            setTimeout(() => {
              self.toggleActiveSwitchMapMarkersIcons()
            }, 500)
          })
        })
      }

      console.log('Control container: ', controlContainer)

      //Лисенер для списка маршрутов
      controlContainer.addEventListener('click', e => {
        const target = e.target

        console.log('Map listeners')
        console.log('target', target)

        if (
          target.classList.contains('bx-chevron-down') ||
          target.classList.contains('bx-chevron-up')
        ) {
          const controlContainer = target.closest('.leaflet-control')
          const chevronIcon = target

          controlContainer.classList.toggle('collapsed')

          if (chevronIcon.classList.contains('bx-chevron-down')) {
            chevronIcon.classList.remove('bx-chevron-down')
            chevronIcon.classList.add('bx-chevron-up')
          } else {
            chevronIcon.classList.remove('bx-chevron-up')
            chevronIcon.classList.add('bx-chevron-down')
          }
        }

        if (
          target.classList.contains('bx-fullscreen') ||
          target.classList.contains('bx-exit-fullscreen')
        ) {
          if (target.classList.contains('bx-fullscreen')) {
            this.MoveMap()
            target.classList.remove('bx-fullscreen')
            target.classList.add('bx-exit-fullscreen')
            target.setAttribute('title', 'Выход из полноэкранного режима')
          } else {
            this.MoveMap()
            target.classList.remove('bx-exit-fullscreen')
            target.classList.add('bx-fullscreen')
            target.setAttribute('title', 'Во весь экран')
          }
        }

        const moskowArmSvgWrapper = target.closest('.moskow-arm-svg-wrapper')
        if (moskowArmSvgWrapper != null) {
          const cityName = 'г. Москва'

          this.fitMapToCityRoutes(cityName)
        }

        if (target.classList.contains('bx-shape-polygon')) {
          this.fitMapToSelectedRoute()
        }

        if (target.classList.contains('fa-map-location-dot')) {
          this.fitMapToSelectedMarker()
        }

        //Кнопки пернесения маркеров или маршрутов в гугл или яндекс карты
        let route = routeService.currentRoute
        let marker = markersService.currentMarker
        const warnWindowShowingTime = 3000
        let warWindowTimer

        console.log('route', route)
        console.log('marker', marker)

        if (target.classList.contains('fa-google')) {
          if (marker == null && route == null) {
            self.showExternalMapWarnWindow(
              warWindowTimer,
              warnWindowShowingTime,
              true
            )
            return
          }

          if (route != null && route.routedata.points.length > 2) {
            let routeUrl = `https://www.google.com/maps/dir/?api=1`
            const routeData = route.routedata
            const points = routeData.points

            const origin = points[0].coordinates.join(',')
            const destination = points[points.length - 1].coordinates.join(',')

            const waypoints = points
              .slice(1, points.length - 1)
              .filter(p => p.role !== 'between')
              .map(p => p.coordinates.join(','))

            routeUrl =
              routeUrl +
              `&origin=${origin}&destination=${destination}&waypoints=${waypoints.join(
                '|'
              )}&travelmode=walking`

            window.open(routeUrl, '_blank')
          } else if (marker != null) {
            const lat = marker._latlng.lat
            const lng = marker._latlng.lng
            const markerUrl = `https://www.google.com/maps/place/${lat},${lng}/@${lat},${lng},19z`
            window.open(markerUrl, '_blank')
          }
        }

        if (target.classList.contains('fa-yandex-international')) {
          if (marker == null && route == null) {
            self.showExternalMapWarnWindow(
              warWindowTimer,
              warnWindowShowingTime,
              false
            )
            return
          }

          if (route != null && route.routedata.points.length > 2) {
            const points = route.routedata.points
            const origin = points[0].coordinates.join(',')
            const destination = points[points.length - 1].coordinates.join(',')

            const waypoints = points
              .slice(1, points.length - 1)
              .filter(p => p.role !== 'between')
              .map(p => p.coordinates.join(','))

            // Собираем rtext по формату: origin~waypoint1~waypoint2~...~destination
            let rtext = origin
            if (waypoints.length > 0) {
              rtext += '~' + waypoints.join('~')
            }
            rtext += '~' + destination

            const routeUrl = `https://yandex.ru/maps/?rtext=${rtext}&rtt=pedestrian&z=19`

            window.open(routeUrl, '_blank')
          } else if (marker != null) {
            const lat = marker._latlng.lat
            const lng = marker._latlng.lng
            const url = `https://yandex.ru/maps/?ll=${lng},${lat}&z=19&pt=${lng},${lat},pm2grml`
            window.open(url, '_blank')
          }
        }
      })

      //Лисенер полноэкранного окна карты
      fullScreenWrapper.addEventListener('click', e => {
        const target = e.target
        const targetId = target.getAttribute('id')

        if (targetId === 'map-fullscreen-exit') {
          const mapControlsExitFullScreenIcon = document.getElementById(
            'map-fullscreen-control-icon'
          )
          mapControlsExitFullScreenIcon.classList.remove('bx-exit-fullscreen')
          mapControlsExitFullScreenIcon.classList.add('bx-fullscreen')
          mapControlsExitFullScreenIcon.setAttribute('title', 'Во весь экран')
          this.MoveMap()
        }
        /*
        //Кнопки пернесения маркеров или маршрутов в гугл или яндекс карты
        let route = routeService.routes[routeService.currentRoute]
        let marker = routeService.currentMarker
        const warnWindowShowingTime = 3000
        let warWindowTimer

        if (target.classList.contains('fa-google')) {
          if (marker == null && route == null) {
            self.showExternalMapWarnWindow(
              warWindowTimer,
              warnWindowShowingTime,
              true
            )
            return
          }

          if (route != null && route.routedata.points.length > 2) {
            let routeUrl = `https://www.google.com/maps/dir/?api=1`
            const routeData = route.routedata
            const points = routeData.points

            const origin = points[0].coordinates.join(',')
            const destination = points[points.length - 1].coordinates.join(',')

            const waypoints = points
              .slice(1, points.length - 1)
              .filter(p => p.role !== 'between')
              .map(p => p.coordinates.join(','))

            routeUrl =
              routeUrl +
              `&origin=${origin}&destination=${destination}&waypoints=${waypoints.join(
                '|'
              )}&travelmode=walking`

            window.open(routeUrl, '_blank')
          } else if (marker != null) {
            const lat = marker._latlng.lat
            const lng = marker._latlng.lng
            const markerUrl = `https://www.google.com/maps/place/${lat},${lng}/@${lat},${lng},19z`
            window.open(markerUrl, '_blank')
          }
        }

        if (target.classList.contains('fa-yandex-international')) {
          if (marker == null && route == null) {
            self.showExternalMapWarnWindow(
              warWindowTimer,
              warnWindowShowingTime,
              false
            )
            return
          }

          if (route != null && route.routedata.points.length > 2) {
            const points = route.routedata.points
            const origin = points[0].coordinates.join(',')
            const destination = points[points.length - 1].coordinates.join(',')

            const waypoints = points
              .slice(1, points.length - 1)
              .filter(p => p.role !== 'between')
              .map(p => p.coordinates.join(','))

            // Собираем rtext по формату: origin~waypoint1~waypoint2~...~destination
            let rtext = origin
            if (waypoints.length > 0) {
              rtext += '~' + waypoints.join('~')
            }
            rtext += '~' + destination

            const routeUrl = `https://yandex.ru/maps/?rtext=${rtext}&rtt=pedestrian&z=19`

            window.open(routeUrl, '_blank')
          } else if (marker != null) {
            const lat = marker._latlng.lat
            const lng = marker._latlng.lng
            const url = `https://yandex.ru/maps/?ll=${lng},${lat}&z=19&pt=${lng},${lat},pm2grml`
            window.open(url, '_blank')
          }
        }
        */
      })

      //Лисенеры popup окона
      mapContainer.addEventListener('click', e => {
        const target = e.target

        if (target.classList.contains('popup-close-icon')) {
          self.deselectMarker()
        }

        if (target.classList.contains('route-popup-close-icon')) {
          self.deselectRoute()
        }

        if (target.classList.contains('popup-multimedia-item')) {
          const pointMultimediaContainer = target.closest(
            '.popup-multimedia-container'
          )
          const imagesContainers =
            pointMultimediaContainer.querySelectorAll('.photo-container')

          imagesContainers.forEach(container => {
            container.classList.remove('active')
          })

          target.closest('.photo-container').classList.add('active')

          this.modalImage.open(self.getCloneHtmlElements(imagesContainers))
        }

        if (target.classList.contains('fit-point-on-map-icon')) {
          self.fitMapToSelectedMarker()
        }

        if (target.classList.contains('current-route-info-icon')) {
          self.toggleActivePointPopupContentContainer('current-route-info-icon')
          self.toggleActivePopupIcon('current-route-info-icon')
        }

        if (target.classList.contains('point-description-icon')) {
          self.toggleActivePointPopupContentContainer('point-description-icon')
          self.toggleActivePopupIcon('point-description-icon')
        }

        if (target.classList.contains('point-images-icon')) {
          self.toggleActivePointPopupContentContainer('point-images-icon')
          self.toggleActivePopupIcon('point-images-icon')
        }

        if (target.classList.contains('point-media-files-icon')) {
          self.toggleActivePointPopupContentContainer('point-media-files-icon')
          self.toggleActivePopupIcon('point-media-files-icon')
        }

        if (target.classList.contains('point-add-media-comment-icon')) {
          self.toggleActivePointPopupContentContainer(
            'point-add-media-comment-icon'
          )
          self.toggleActivePopupIcon('point-add-media-comment-icon')
        }

        if (target.classList.contains('point-write-comment-icon')) {
          self.toggleActivePointPopupContentContainer(
            'point-write-comment-icon'
          )
          self.toggleActivePopupIcon('point-write-comment-icon')
        }

        if (target.classList.contains('next-point-icon')) {
          self.toggleActiveMarker(target.classList)
        }

        if (target.classList.contains('prev-point-icon')) {
          self.toggleActiveMarker(target.classList)
        }
      })
    }
  }

  getContainerId() {
    return this.map.getContainer().id
  }

  zoomToRoute(routeId) {
    const route = this.routesService[routeId]
    if (!route) return

    this.map.flyToBounds(route.getBounds(), {
      padding: [30, 30],
      duration: 2,
      easeLinearity: 0.25,
      maxZoom: 14
    })
  }

  fitMapToVisibleRoutes() {
    const bounds = new L.LatLngBounds()
    let hasVisible = false

    for (const route of Object.values(this.routesService.routes)) {
      if (this.map.hasLayer(route.shape)) {
        bounds.extend(route.shape.getBounds())
        hasVisible = true
      }
    }

    if (hasVisible) {
      this.map.fitBounds(bounds, { padding: [20, 20], maxZoom: 14 })
    }
  }

  fitMapToCityRoutes(cityName) {
    const groupedRoutesByCity = this.routesService.groupeRoutesByCity()
    const bounds = new L.LatLngBounds()

    for (const [city, cityRoutes] of Object.entries(groupedRoutesByCity)) {
      if (city === cityName) {
        cityRoutes.forEach(({ routeId, route }) => {
          bounds.extend(route.shape.getBounds())
        })
      }
    }
    if (bounds.isValid()) {
      this.map.fitBounds(bounds, { padding: [20, 20], maxZoom: 14 })
    }
  }

  fitMapToSelectedRoute() {
    const route = this.routesService.currentRoute

    if (route === null) return

    const bounds = route.shape.getBounds()
    this.map.fitBounds(bounds, { padding: [20, 20], maxZoom: 17 })
  }

  fitMapToSelectedMarker() {
    const marker = this.markersService.currentMarker
    if (!marker) return

    const latlng = marker.getLatLng()
    this.map.setView(latlng, 18)
  }

  //Функция переноса контейнера карты за рамки контейнера контента
  MoveMap() {
    const mapWrapper = this.originalMapWrapper
    const mapContainer = this.map.getContainer()
    const fullScreenWrapper = document.getElementById('map-fullscreen-wrapper')
    const fullScreenContainer = document.getElementById(
      'map-fullscreen-container'
    )

    if (mapWrapper.classList.contains('content-map-wrapper')) {
      mapWrapper.removeChild(mapContainer)
      mapWrapper.classList.remove('content-map-wrapper')
      fullScreenWrapper.classList.add('show')
      fullScreenContainer.classList.add('content-map-wrapper')
      fullScreenContainer.appendChild(mapContainer)
      clearTimeout(timer)
      const timer = setTimeout(() => {
        this.fitMapToVisibleRoutes()
        this.map.invalidateSize()
      }, 300)
    } else {
      fullScreenContainer.removeChild(mapContainer)
      fullScreenWrapper.classList.remove('show')
      fullScreenContainer.classList.remove('content-map-wrapper')
      mapWrapper.classList.add('content-map-wrapper')
      mapWrapper.appendChild(mapContainer)
      clearTimeout(timer)
      const timer = setTimeout(() => {
        this.fitMapToVisibleRoutes()
        this.map.invalidateSize()
      }, 300)
    }
  }

  //Функции переключение выделения маршрута
  selectRoute(routeId) {
    if (routeId === null) return

    const routeService = this.routesService

    const shape = routeService.routes[routeId].shape

    routeService.selectRoute(routeId)
    shape.openPopup()

    this.toggleSelectingRowInActiveContentFormat(routeId)
  }

  deselectRoute() {
    const routeService = this.routesService

    if (routeService.currentRoute == null) return

    const shape = routeService.currentRoute.shape

    console.log('routeId: ', routeService.currentRoute.routedata.routeId)

    this.toggleSelectingRowInActiveContentFormat(
      routeService.currentRoute.routedata.routeId
    )

    routeService.deselectRoute()
    shape.closePopup()
  }

  showOrHideRoute(routeId) {
    const map = this.map
    const route = this.routesService.routes[routeId]

    if (route.visible) {
      map.removeLayer(route.shape)
      route.visible = false
      this.showOrHideMarkers(route.markers, false)
    } else {
      route.shape.addTo(this.map)
      route.visible = true
      this.showOrHideMarkers(route.markers, true)
    }
  }

  //Функции переключение выделения маркера
  selectMarker(marker) {
    if (marker === null) return

    const markersService = this.markersService

    markersService.selectMarker(marker)
    marker.openPopup()

    this.toggleActiveSwitchMapMarkersIcons()
  }

  deselectMarker() {
    const markersService = this.markersService

    if (markersService.currentMarker == null) return

    markersService.currentMarker.closePopup()
    markersService.deselectMarker()
  }

  showOrHideMarkers(markers, isShow) {
    const map = this.map
    if (isShow) {
      markers.forEach(marker => marker.addTo(map))
    } else {
      markers.forEach(marker => map.removeLayer(marker))
    }
  }

  //Выделяем строку в активном формате контента
  toggleSelectingRowInActiveContentFormat(routeId) {
    const element =
      this.contentItemsService.activeContentItem.item.querySelector(
        `i[route-id="${routeId}"]`
      )
    this.contentItemsService.toggleSelectedContentItemDataRow(element)
  }

  //Функция показа предупреждения для перехода на внешнюю карту
  showExternalMapWarnWindow(
    warWindowTimer,
    warnWindowShowingTime,
    isGoogleMaps
  ) {
    const externalMapsContainer = document.querySelector(
      '.leaflet-container-external-maps-control'
    )

    let warningWindow

    if (isGoogleMaps) {
      warningWindow = document.querySelector('.google-maps-warning-window')
    } else {
      warningWindow = document.querySelector('.yandex-maps-warning-window')
    }

    const top = externalMapsContainer.getBoundingClientRect().top
    const left =
      externalMapsContainer.getBoundingClientRect().left -
      warningWindow.getBoundingClientRect().width

    warningWindow.style.top = top + 'px'
    warningWindow.style.left = left - 10 + 'px'

    warningWindow.classList.add('active')

    clearTimeout(warWindowTimer)

    warWindowTimer = setTimeout(() => {
      warningWindow.classList.remove('active')
    }, warnWindowShowingTime)
  }

  toggleActiveMarker(iconClassList) {
    const self = this
    const markersService = this.markersService
    let toggleMarker

    //Присваиваем маркер
    if (iconClassList.contains('next-point-icon')) {
      toggleMarker = markersService.nextMarker
    } else if (iconClassList.contains('prev-point-icon')) {
      toggleMarker = markersService.prevMarker
    }

    //Закрываем попап текущего маркера
    markersService.currentMarker.closePopup()

    //Выбираем следующий маркер
    markersService.selectMarker(toggleMarker)

    self.fitMapToSelectedMarker()

    //Открываем попап следующего маркера
    markersService.currentMarker.openPopup()
  }

  toggleActiveSwitchMapMarkersIcons() {
    const mapContainer = this.map.getContainer()
    const markersService = this.markersService
    const prevIcon = mapContainer.querySelector('.prev-point-icon')
    const nextIcon = mapContainer.querySelector('.next-point-icon')

    if (markersService.prevMarker === null) {
      prevIcon.classList.remove('active')
    } else {
      prevIcon.classList.add('active')
    }

    if (markersService.nextMarker === null) {
      nextIcon.classList.remove('active')
    } else {
      nextIcon.classList.add('active')
    }
  }

  toggleActivePointPopupContentContainer(iconClassName) {
    const cntContainerId = this.getPopupContentId(iconClassName)

    const cntContainer = document.getElementById(cntContainerId)

    if (cntContainer != null) {
      this.deactivatePopupContentContainer()

      cntContainer.classList.add('show')
    }
  }

  deactivatePopupContentContainer() {
    const contentContainerId = this.getPopupContentId(
      this.markersService.currentMarker.options.activeIcon
    )
    const pointPopupContainer = document.getElementById(contentContainerId)

    if (pointPopupContainer != null) {
      pointPopupContainer.classList.remove('show')
    }
  }

  toggleActivePopupIcon(iconClassName) {
    const mapContainer = this.map.getContainer()
    const activeIconClassName =
      this.markersService.currentMarker.options.activeIcon
    const icon = mapContainer.querySelector(`.${iconClassName}`)
    const activeIcon = mapContainer.querySelector(`.${activeIconClassName}`)

    if (activeIcon === icon) return

    if (activeIcon != null) {
      activeIcon.classList.remove('selected')
    }

    if (icon != null) {
      this.markersService.currentMarker.options.activeIcon = iconClassName
      icon.classList.add('selected')
    }
  }

  getPopupContentId(iconClassName) {
    return iconClassName.substring(0, iconClassName.indexOf('-icon'))
  }

  //HTML элементы
  setPopupForShape() {
    const routes = this.routesService.routes

    for (const [routeId, route] of Object.entries(routes)) {
      const shape = route.shape

      shape.bindPopup(this.getShapePopupElement(route.routedata))

      //Всплывающее окно при наведении на маркер
      const shapeToolTipElement = this.getShapeToolTipElement(route.routedata)

      shape.bindTooltip(shapeToolTipElement, {
        permanent: false, // false — только при наведении
        direction: 'top', // top, right, bottom, left
        offset: [0, 0], // смещение подсказки
        className: 'shape-tooltip-wrapper'
      })
    }
  }

  setTooltipToShape() {
    const routes = this.routesService.routes

    for (const [routeId, route] of Object.entries(routes)) {
      const shape = route.shape

      //Всплывающее окно при наведении на маркер
      const shapeToolTipElement = this.getShapeToolTipElement(route.routedata)

      shape.bindTooltip(shapeToolTipElement, {
        permanent: false, // false — только при наведении
        direction: 'top', // top, right, bottom, left
        offset: [0, 0], // смещение подсказки
        className: 'shape-tooltip-wrapper'
      })
    }
  }

  setPopupForMarkers() {
    const routeMarkers = this.markersService.routeMarkers

    for (const [routeId, markers] of Object.entries(routeMarkers)) {
      markers.forEach(marker => {
        const markerData = marker.options.markerData
        let markerInfoHtmlElement

        if (
          markerData.role === 'between' ||
          markerData.role === 'between-multimedia'
        ) {
          markerInfoHtmlElement = this.getBetweenPointPopupElement(marker)
        } else {
          markerInfoHtmlElement = this.getPointPopupElement(marker)
        }

        //Всплывающее окно при клике на маркер
        marker.bindPopup(markerInfoHtmlElement, {
          offset: [-280, 0]
        })
      })
    }
  }

  setTooltipForMarkers() {
    const routeMarkers = this.markersService.routeMarkers

    for (const [routeId, markers] of Object.entries(routeMarkers)) {
      markers.forEach(marker => {
        //Всплывающее окно при наведении на маркер
        const markerToolTipElement = this.getPointToolTipElement(marker)

        marker.bindTooltip(markerToolTipElement, {
          permanent: false, // false — только при наведении
          direction: 'top', // top, right, bottom, left
          offset: [5, -30], // смещение подсказки
          className: 'point-tooltip-wrapper'
        })
      })
    }
  }

  async getMoskowSvgElement() {
    const svgWrapper = document.createElement('div')
    svgWrapper.classList.add('moskow-arm-svg-wrapper')
    svgWrapper.setAttribute('title', 'Показать все маршруты Москвы')

    try {
      const response = await fetch('./images/icon/Moskow_arm.svg')
      const svg = await response.text()

      svgWrapper.innerHTML = svg
      return svgWrapper
    } catch (error) {
      console.log(`Ошибка загрузки Moskow-arm.svg: ${error}`)
    }
  }

  async getTverSvgElement() {
    const svgWrapper = document.createElement('div')
    svgWrapper.classList.add('tver-arm-svg-wrapper')
    svgWrapper.setAttribute('title', 'Показать все маршруты Твери')

    try {
      const response = await fetch('./images/icon/Tver_arm.svg')
      const svg = await response.text()

      svgWrapper.innerHTML = svg
      return svgWrapper
    } catch (error) {
      console.log(`Ошибка загрузки Tver_arm.svg: ${error}`)
    }
  }

  //---------------------------------------------------------------------------------------------------------------
  //Popup для маршрута
  getShapePopupElement(routedata) {
    const shapePopupWrapper = document.createElement('div')
    const shapePopupContainer = document.createElement('div')
    const popupCloseIcon = document.createElement('i')
    const routeName = document.createElement('h4')
    const routeDescriptionContainer = document.createElement('div')
    const routeBriefDescriptionContainer = document.createElement('div')

    shapePopupWrapper.classList.add('map-popup-wrapper')
    shapePopupContainer.classList.add('map-popup-container', 'show')
    shapePopupContainer.setAttribute('id', 'route-description')
    popupCloseIcon.classList.add('route-popup-close-icon', 'bx', 'bx-x')
    routeName.classList.add('route-name')
    routeName.innerHTML = routedata.routeName
    routeDescriptionContainer.classList.add('route-description-container')
    routeDescriptionContainer.innerHTML = routedata.routeDescription
    routeBriefDescriptionContainer.classList.add(
      'route-brief-description-container'
    )
    routeBriefDescriptionContainer.innerHTML = routedata.routeBriefDescription

    shapePopupContainer.appendChild(routeName)
    shapePopupContainer.appendChild(routeDescriptionContainer)
    shapePopupContainer.appendChild(routeBriefDescriptionContainer)

    shapePopupWrapper.appendChild(popupCloseIcon)
    shapePopupWrapper.appendChild(shapePopupContainer)

    return shapePopupWrapper
  }

  //---------------------------------------------------------------------------------------------------------------
  //Tooltip для маршрута
  getShapeToolTipElement(routedata) {
    const tooltipContainer = document.createElement('div')
    const routeName = document.createElement('h3')
    const additionalInfo = document.createElement('p')
    const additionalInfoText =
      'Для получения более полной информации нажмите на маршрут'

    tooltipContainer.classList.add('shape-tooltip-container')
    routeName.classList.add('route-name')
    routeName.innerHTML = `Маршрут: ${routedata.routeName}`
    additionalInfo.classList.add('route-additional-info')
    additionalInfo.innerHTML = additionalInfoText

    tooltipContainer.appendChild(routeName)
    tooltipContainer.appendChild(additionalInfo)

    return tooltipContainer
  }

  //---------------------------------------------------------------------------------------------------------------
  //---------------------------------------------------------------------------------------------------------------
  //Popup для точек маршрута
  getPointPopupElement(marker) {
    const pointPopupWrapper = document.createElement('div')
    const popupCloseIcon = document.createElement('i')

    const iconsContainer = this.getPointPopupIconsHtmlContainer()
    const pointDescriptionContainer =
      this.getPointPopupDescriptionHtmlElement(marker)
    const pointRouteInfoContainer =
      this.getPointPopupCurrentRouteInfoHtmlElement(marker)
    const pointPopupImagesContainer = this.getPointImagesHtmlElement(marker)
    const pointPopupMediaFilesContainer =
      this.getPointMediaFilesHtmlElement(marker)
    const pointPopupWritwCommentContainer =
      this.getPointWriteCommentHtmlElement()
    const pointPopupAddMediaFilesContainer = this.getPointAddImageHtmlElement()

    pointPopupWrapper.classList.add('map-popup-wrapper')
    pointPopupWrapper.setAttribute('point-type', 'point')
    popupCloseIcon.classList.add('popup-close-icon', 'bx', 'bx-x')

    pointPopupWrapper.appendChild(popupCloseIcon)
    pointPopupWrapper.appendChild(pointDescriptionContainer)
    pointPopupWrapper.appendChild(pointRouteInfoContainer)
    pointPopupWrapper.appendChild(pointPopupMediaFilesContainer)
    pointPopupWrapper.appendChild(pointPopupImagesContainer)
    pointPopupWrapper.appendChild(pointPopupWritwCommentContainer)
    pointPopupWrapper.appendChild(pointPopupAddMediaFilesContainer)
    pointPopupWrapper.appendChild(iconsContainer)

    return pointPopupWrapper
  }

  getPointPopupIconsHtmlContainer() {
    const iconsContainer = document.createElement('div')
    const currentRouteInfoIcon = document.createElement('i')
    const fitPointOnMapIcon = document.createElement('i')
    const pointDescriptionIcon = document.createElement('i')
    const pointMediaFilesIcon = document.createElement('i')
    const pointImagesIcon = document.createElement('i')
    const pointWriteCommentIcon = document.createElement('i')
    const pointAddMediaComment = document.createElement('i')
    const chevronsContainer = document.createElement('div')
    const nextPintIcon = document.createElement('i')
    const prevPointIcon = document.createElement('i')

    iconsContainer.classList.add('icons-container')
    currentRouteInfoIcon.classList.add(
      'current-route-info-icon',
      'fa-solid',
      'fa-house-flag'
    )
    currentRouteInfoIcon.setAttribute('title', 'Описание маршрута')
    fitPointOnMapIcon.classList.add('fit-point-on-map-icon', 'bx', 'bx-map-pin')
    fitPointOnMapIcon.setAttribute('title', 'Показать выбранную точку на карте')
    pointDescriptionIcon.classList.add(
      'point-description-icon',
      'fa-solid',
      'fa-file-pen',
      'selected'
    )
    pointDescriptionIcon.setAttribute('title', 'Описание выбранной точки')
    pointMediaFilesIcon.classList.add(
      'point-media-files-icon',
      'bx',
      'bx-headphone'
    )
    pointMediaFilesIcon.setAttribute('title', 'Аудио к выбранной точке')
    pointImagesIcon.classList.add('point-images-icon', 'bx', 'bxs-file-image')
    pointImagesIcon.setAttribute('title', 'Изображения к выбранной точке')
    pointWriteCommentIcon.classList.add(
      'point-write-comment-icon',
      'bx',
      'bx-comment-add'
    )
    pointWriteCommentIcon.setAttribute('title', 'Написать комментарии')
    pointAddMediaComment.classList.add(
      'point-add-media-comment-icon',
      'bx',
      'bx-image-add'
    )
    pointAddMediaComment.setAttribute('title', 'Добавить медиафайлы')

    chevronsContainer.classList.add('chevrons-container')
    prevPointIcon.classList.add('prev-point-icon', 'bx', 'bx-chevron-left')
    prevPointIcon.setAttribute('title', 'Предыдущая точка маршрута')
    nextPintIcon.classList.add('next-point-icon', 'bx', 'bx-chevron-right')
    nextPintIcon.setAttribute('title', 'Следующая точка маршрута')
    chevronsContainer.appendChild(prevPointIcon)
    chevronsContainer.appendChild(nextPintIcon)

    iconsContainer.appendChild(currentRouteInfoIcon)
    iconsContainer.appendChild(fitPointOnMapIcon)
    iconsContainer.appendChild(pointDescriptionIcon)
    iconsContainer.appendChild(pointMediaFilesIcon)
    iconsContainer.appendChild(pointImagesIcon)
    iconsContainer.appendChild(pointWriteCommentIcon)
    iconsContainer.appendChild(pointAddMediaComment)
    iconsContainer.appendChild(chevronsContainer)

    return iconsContainer
  }

  getPointPopupDescriptionHtmlElement(marker) {
    const markerData = marker.options.markerData
    const routeData = this.routesService.routes[markerData.routeId].routedata

    const container = document.createElement('div')
    const routeName = document.createElement('h4')
    const pointName = document.createElement('h3')
    const pointKey = document.createElement('p')
    const routeComment = document.createElement('p')
    const pointAddress = document.createElement('p')

    container.classList.add('map-popup-container', 'show')
    container.setAttribute('id', 'point-description')

    routeName.classList.add('route-name')
    routeName.innerHTML = routeData.routeName
    pointName.classList.add('point-name')
    pointName.innerHTML = markerData.name
    pointKey.classList.add('point-key')
    pointKey.innerHTML = markerData.key
    routeComment.classList.add('route-comment')
    routeComment.innerHTML = routeData.routeComment
    pointAddress.classList.add('point-address')
    pointAddress.innerHTML = `<b>Адрес</b>: ${markerData.address}`

    container.appendChild(routeName)
    container.appendChild(pointName)
    container.appendChild(pointKey)
    container.appendChild(routeComment)
    container.appendChild(pointAddress)

    return container
  }

  getPointPopupCurrentRouteInfoHtmlElement(marker) {
    const markerData = marker.options.markerData
    const routeData = this.routesService.routes[markerData.routeId].routedata
    const container = document.createElement('div')
    const contentTitle = document.createElement('h4')
    const routeDescription = document.createElement('p')

    container.classList.add('map-popup-cnt-container')
    container.setAttribute('id', 'current-route-info')
    contentTitle.classList.add('content-title')
    contentTitle.innerHTML = 'Описание маршрута'
    routeDescription.classList.add('content-description')
    routeDescription.innerHTML = routeData.routeDescription
    container.appendChild(contentTitle)
    container.appendChild(routeDescription)

    return container
  }

  getPointImagesHtmlElement(marker) {
    const markerData = marker.options.markerData
    const photoPaths = markerData.photoPaths
    const container = document.createElement('div')
    const contentTitle = document.createElement('h4')
    const multimediaContainer = this.getMultimediaHtmlContainer(
      markerData,
      photoPaths
    )

    container.classList.add('map-popup-cnt-container')
    container.setAttribute('id', 'point-images')

    contentTitle.classList.add('content-title')
    contentTitle.innerHTML = 'Изображения'

    container.appendChild(contentTitle)

    container.appendChild(multimediaContainer)

    return container
  }

  getMultimediaHtmlContainer(markerData, photoPaths) {
    const multimediaContainer = document.createElement('div')
    const noImagesTitle = document.createElement('h4')

    multimediaContainer.classList.add(
      'popup-multimedia-container',
      'point-multimedia-container'
    )

    if (photoPaths != null && photoPaths.length > 0) {
      photoPaths.forEach(path => {
        const photoContainer = document.createElement('div')
        const photo = document.createElement('img')
        const photoTitle = document.createElement('h5')

        photoContainer.classList.add('photo-container')

        if (markerData.photoDescriptions[photoPaths.indexOf(path)]) {
          photoTitle.classList.add('photo-title')
          photoTitle.innerHTML =
            markerData.photoDescriptions[photoPaths.indexOf(path)]

          photoContainer.appendChild(photoTitle)
        }

        photo.src = path
        photo.classList.add('popup-multimedia-item')

        photoContainer.appendChild(photo)

        multimediaContainer.appendChild(photoContainer)
      })
    } else {
      noImagesTitle.classList.add('no-images-title', 'no-content-title')
      noImagesTitle.innerHTML = 'К данной точке изображений нет'
      multimediaContainer.appendChild(noImagesTitle)
    }

    return multimediaContainer
  }

  getPointMediaFilesHtmlElement(marker) {
    const markerData = marker.options.markerData
    const routeData = this.routesService.routes[markerData.routeId].routedata

    const container = document.createElement('div')
    const contentTitle = document.createElement('h4')
    const noMediaFilesTitle = document.createElement('h4')

    container.classList.add('map-popup-cnt-container')
    container.setAttribute('id', 'point-media-files')

    contentTitle.classList.add('content-title')
    contentTitle.innerHTML = 'Медиафайлы'

    noMediaFilesTitle.classList.add('no-media-files-title', 'no-content-title')
    noMediaFilesTitle.innerHTML = 'К данной точке медиафайлов нет'

    container.appendChild(contentTitle)
    container.appendChild(noMediaFilesTitle)

    return container
  }

  getPointWriteCommentHtmlElement() {
    const container = document.createElement('div')
    const form = document.createElement('form')
    const contentTitle = document.createElement('h4')
    const taxtarea = document.createElement('textarea')
    const button = document.createElement('button')

    container.classList.add('map-popup-cnt-container')
    container.setAttribute('id', 'point-write-comment')

    form.classList.add('map-popup-form')

    contentTitle.classList.add('content-title')
    contentTitle.innerHTML = 'Написать комментарий к точке маршрута'
    taxtarea.setAttribute('type', 'text')
    taxtarea.setAttribute('placeholder', 'Введите комментарии')

    button.classList.add('map-popup-control-button', 'btn')
    button.setAttribute('type', 'button')
    button.innerHTML = 'Сохранить'

    form.appendChild(contentTitle)
    form.appendChild(taxtarea)
    form.appendChild(button)

    container.appendChild(form)

    return container
  }

  getPointAddImageHtmlElement() {
    const container = document.createElement('div')
    const form = document.createElement('form')
    const contentTitle = document.createElement('h4')
    const input = document.createElement('input')
    const button = document.createElement('button')

    container.classList.add('map-popup-cnt-container')
    container.setAttribute('id', 'point-add-media-comment')

    form.classList.add('map-popup-form')

    contentTitle.classList.add('content-title')
    contentTitle.innerHTML = 'Добавить медиафайл'
    input.setAttribute('type', 'file')
    input.setAttribute('placeholder', 'Выбирете файл для загрузки')

    button.classList.add('map-popup-control-button', 'btn')
    button.setAttribute('type', 'button')
    button.innerHTML = 'Загрузить'

    form.appendChild(contentTitle)
    form.appendChild(input)
    form.appendChild(button)

    container.appendChild(form)

    return container
  }

  isPopupOpen() {
    const mapContainer = this.map.getContainer()
    const popupContainer = mapContainer.querySelector('.map-popup-container')

    console.log('popupContainer', popupContainer)

    if (popupContainer != null) {
      return true
    }

    return false
  }

  //---------------------------------------------------------------------------------------------------------------
  //---------------------------------------------------------------------------------------------------------------
  //Popup для промежуточных точек маршрута
  getBetweenPointPopupElement(marker) {
    const markerData = marker.options.markerData
    const photoPaths = markerData.photoPaths
    const routeData = this.routesService.routes[markerData.routeId].routedata
    const pointPopupWrapper = document.createElement('div')
    const pointPopupContainer = document.createElement('div')
    const pointPopupCntContainer = document.createElement('div')
    const popupCloseIcon = document.createElement('i')
    const routeName = document.createElement('h4')
    const pointName = document.createElement('h3')
    const multimediaContainer = this.getMultimediaHtmlContainer(
      markerData,
      photoPaths
    )
    const iconsContainer = this.getBetweenPointPopupIconsHtmlContainer()

    pointPopupWrapper.classList.add('map-popup-wrapper')
    pointPopupWrapper.setAttribute('point-type', 'between')
    pointPopupContainer.classList.add('map-popup-container', 'show')
    pointPopupCntContainer.classList.add(
      'map-popup-default-info-container',
      'show'
    )
    popupCloseIcon.classList.add('popup-close-icon', 'bx', 'bx-x')
    routeName.classList.add('route-name')
    routeName.innerHTML = routeData.routeName
    pointName.classList.add('point-name')
    pointName.innerHTML = markerData.name

    pointPopupContainer.appendChild(popupCloseIcon)
    pointPopupCntContainer.appendChild(routeName)
    pointPopupCntContainer.appendChild(pointName)
    pointPopupCntContainer.appendChild(multimediaContainer)
    pointPopupContainer.appendChild(pointPopupCntContainer)
    pointPopupWrapper.appendChild(pointPopupContainer)
    pointPopupWrapper.appendChild(iconsContainer)

    return pointPopupWrapper
  }

  getBetweenPointPopupIconsHtmlContainer() {
    const iconsContainer = document.createElement('div')
    const fitPointOnMapIcon = document.createElement('i')
    const chevronsContainer = document.createElement('div')
    const nextPointIcon = document.createElement('i')
    const prevPointIcon = document.createElement('i')

    iconsContainer.classList.add('icons-container')

    fitPointOnMapIcon.classList.add('fit-point-on-map-icon', 'bx', 'bx-map-pin')
    fitPointOnMapIcon.setAttribute('title', 'Показать выбранную точку на карте')

    chevronsContainer.classList.add('chevrons-container')
    nextPointIcon.classList.add('next-point-icon', 'bx', 'bx-chevron-right')
    nextPointIcon.setAttribute('title', 'Следующая точка маршрута')
    prevPointIcon.classList.add('prev-point-icon', 'bx', 'bx-chevron-left')
    prevPointIcon.setAttribute('title', 'Предыдущая точка маршрута')
    chevronsContainer.appendChild(prevPointIcon)
    chevronsContainer.appendChild(nextPointIcon)

    iconsContainer.appendChild(fitPointOnMapIcon)
    iconsContainer.appendChild(chevronsContainer)

    return iconsContainer
  }

  //---------------------------------------------------------------------------------------------------------------
  //---------------------------------------------------------------------------------------------------------------
  //Tooltip для точек маршрута
  getPointToolTipElement(marker) {
    const markerData = marker.options.markerData
    const tooltipContainer = document.createElement('div')
    const pointName = document.createElement('h3')
    const additionalInfo = document.createElement('p')
    const additionalInfoText =
      'Для получения более полной информации нажмите на маркер точки'

    tooltipContainer.classList.add('point-tooltip-container')
    pointName.classList.add('point-name')
    pointName.innerHTML = markerData.name
    additionalInfo.classList.add('point-additional-info')
    additionalInfo.innerHTML = additionalInfoText

    tooltipContainer.appendChild(pointName)
    tooltipContainer.appendChild(additionalInfo)
    return tooltipContainer
  }

  //---------------------------------------------------------------------------------------------------------------
  //---------------------------------------------------------------------------------------------------------------
  //Вспомогательные функции
  getCloneHtmlElements(elements) {
    const clones = Array.from(elements, element => {
      return element.cloneNode(true)
    })
    return clones
  }
}

export class RoutesMapService extends BaseMapService {
  originalMapWrapper
  map
  mapType
  routesService
  markersService
  contentItemsService
  modal
  modalImage

  async create(mapContainerId, jsonUrl) {
    //Создание карты
    super.createMap(mapContainerId)
    this.mapType = 'routes-map'
    const routesService = new RouteService()
    await routesService.loadRoutes(jsonUrl)
    this.routesService = routesService

    this.markersService = this.routesService.markersService

    super.populateMap()
    this.addRouteDataToContentContainers()

    super.setPopupForShape()
    super.setTooltipToShape()
    super.setPopupForMarkers()
    super.setTooltipForMarkers()

    this.originalMapWrapper = this.map.getContainer().parentElement

    //Инициализация сервисов управления контентом
    const cntId = document.getElementById(mapContainerId).closest('.sb-cnt').id

    this.contentItemsService = contentService.getContentItemsService(cntId)

    //Инициализация модальных окон
    this.modal = new Modal()
    this.modalImage = new ModalImage()

    //Создание слушателей и контролов
    super.createMapListeners()
    super.createMapControls()
  }

  get map() {
    return this.map
  }

  getMapContentConteinerId() {
    return this.map.getContainer().closest('.sb-cnt').id
  }

  getRoute(routeId) {
    return this.routesService.routes[routeId]
  }

  getRouteData(routeId) {
    return this.routesService.routes[routeId].routedata
  }

  isContainsRoute(routeId) {
    if (Object.keys(this.routesService.routes).includes(routeId)) {
      return true
    } else {
      return false
    }
  }

  addRouteDataToContentContainers() {
    const routes = this.routesService.routes
    const table = document.getElementById('pilgrimage-tbl')
    const tableRows = table.querySelectorAll('.tbl-body-row')
    const title = document.getElementById('pilgrimage-title')
    const titleItems = title.querySelectorAll('.sb-cnt-title-container-item')

    for (const [routeId, route] of Object.entries(routes)) {
      const routeData = route.routedata
      //Заполнение таблицы данными маршрута
      const row = tableRows[0]
      const rowItems = row.querySelectorAll('.tbl-row-data')

      if (rowItems[0].textContent === '') {
        rowItems[0].innerHTML = routeData.routeName
        rowItems[1].innerHTML = routeData.routeBriefDescription

        //Добавляем id маршрута к иконке показать на карте
        row
          .querySelector('.bx-map-pin')
          .setAttribute('route-id', routeData.routeId)
      } else {
        const newRow = row.cloneNode(true)
        newRow.setAttribute('obj-id', tableRows.length + 1)

        const newRowItems = newRow.querySelectorAll('.tbl-row-data')

        newRowItems[0].innerHTML = routeData.routeName
        newRowItems[1].innerHTML = routeData.routeBriefDescription

        //Добавляем id маршрута к иконке показать на карте
        newRow
          .querySelector('.bx-map-pin')
          .setAttribute('route-id', routeData.routeId)

        table.querySelector('tbody').appendChild(newRow)
      }

      //Заполнение плитки данными маршрута

      const item = titleItems[0]
      const itemData = item.querySelectorAll('.title-data')

      if (itemData[0].textContent === '') {
        itemData[0].innerHTML = routeData.routeName
        itemData[1].innerHTML = routeData.routeBriefDescription

        item
          .querySelector('.bx-map-pin')
          .setAttribute('route-id', routeData.routeId)
      } else {
        const newItem = item.cloneNode(true)

        newItem.setAttribute('obj-id', titleItems.length + 1)

        const newItemData = newItem.querySelectorAll('.title-data')

        newItemData[0].innerHTML = routeData.routeName
        newItemData[1].innerHTML = routeData.routeBriefDescription

        newItem
          .querySelector('.bx-map-pin')
          .setAttribute('route-id', routeData.routeId)
        newItem.setAttribute('obj-id', titleItems.length + 1)

        const lastChild = title.lastElementChild

        title.insertBefore(newItem, lastChild)
      }
    }
  }
}

export class OneRouteMapService extends BaseMapService {
  originalMapWrapper
  map
  mapType
  routesService
  markersService
  contentItemsService
  modal
  modalImage

  create(mapContainerId, route) {
    super.createMap(mapContainerId)

    this.mapType = 'one-route-map'
    const routeService = new RouteService()
    routeService.init(route)
    this.routesService = routeService
    this.markersService = routeService.markersService

    super.populateMap()

    super.setPopupForShape()
    super.setTooltipToShape()
    super.setPopupForMarkers()
    super.setTooltipForMarkers()

    this.originalMapWrapper = this.map.getContainer().parentElement

    //Инициализация сервисов управления контентом
    const cntId = document.getElementById(mapContainerId).closest('.sb-cnt').id

    this.contentItemsService = contentService.getContentItemsService(cntId)

    //Инициализация модальных окон
    this.modal = new Modal()
    this.modalImage = new ModalImage()

    //Создание слушателей и контролов
    super.createMapListeners()
    super.createMapControls()
  }

  getMap() {
    return this.map
  }

  getMapContentConteinerId() {
    return this.map.getContainer().closest('.sb-cnt').id
  }

  getRouteId() {
    return this.routesService.routes[0].routedata.routeId
  }
}

//Функции для лисенеров клавиатуры
function handleMapNavigation(e, mapServices) {
  mapServices.forEach(mapService => {
    //console.log('mapService:', mapService)
    const mapContainer = mapService.map.getContainer()
    const routeService = mapService.mapRoutes
    const nextIcon = mapContainer.querySelector('.next-point-icon')
    const prevIcon = mapContainer.querySelector('.prev-point-icon')

    if (e.key === 'ArrowRight') {
      if (mapService.isPopupOpen() && !mapService.modalImage.isModalOpen()) {
        e.preventDefault() // Отключаем действие по умолчанию (scroll карты)
        e.stopPropagation()

        mapService.toggleActiveMarker(nextIcon.classList)
      }
    } else if (e.key === 'ArrowLeft') {
      if (mapService.isPopupOpen() && !mapService.modalImage.isModalOpen()) {
        e.preventDefault() // Отключаем действие по умолчанию (scroll карты)
        e.stopPropagation()

        mapService.toggleActiveMarker(prevIcon.classList)
      }
    } else if (e.key === 'Escape') {
      const mapPhotoModalWrapper = document.getElementById(
        'map-photo-modal-wrapper'
      )

      if (
        mapPhotoModalWrapper != null &&
        mapPhotoModalWrapper.classList.contains('show')
      ) {
        //console.log('Закрытие модального окна')
        mapService.modalImage.closePhotoModal()
        return
      }
    }
  })
}

class RouteService {
  routes
  currentRoute

  shapeStyles

  markersService

  async loadRoutes(jsonUrl) {
    const routes = await this.loadRoutesFromJsonFile(jsonUrl)
    this.routes = routes

    return routes
  }

  async loadRoutesFromJsonFile(jsonUrl) {
    try {
      const self = this
      const response = await fetch(jsonUrl)
      const data = await response.json()
      const routes = data.routes
      const mapRoutes = {}
      const markersService = new MarkerService()

      if (routes && Array.isArray(routes)) {
        routes.forEach(route => {
          if (route.points && Array.isArray(route.points)) {
            markersService.createRouteMarkers(route)
            const markers = markersService.routeMarkers[route.routeId]
            const markerStyles = markersService.markerStyles

            // Создаем полилинию по всем точкам
            self.shapeStyles = self.createShapeStyles(route.shapeColor)
            const shape = self.createRouteShape(route)

            mapRoutes[route.routeId] = {
              shape,
              markers,
              shapeStyles: self.shapeStyles,
              markerStyles,
              isSelected: false,
              visible: true,
              routedata: route
            }
          }
        })

        self.markersService = markersService

        return mapRoutes
      } else {
        console.error('Некорректный формат данных в JSON!')
      }
    } catch (error) {
      console.error('Ошибка загрузки маршрута:', error)
    }
  }

  init(route) {
    console.log('Инициализация маршрута')
    this.shapeStyles = route.shapeStyles
    const newRoute = route
    newRoute.shape = this.createRouteShape(route.routedata)
    this.currentRoute = newRoute
    const markersService = new MarkerService()
    markersService.init(route)
    this.markersService = markersService

    this.routes = { [route.routedata.routeId]: newRoute }
    console.log('this.routes', this.routes)
  }

  //Создание объекта маршрута
  createRouteShape(route) {
    let result
    const coordinates = []
    const defaultStyle = this.shapeStyles.defaultStyle

    route.points.forEach(point => coordinates.push(point.coordinates))

    switch (route.type) {
      case 'polyline':
        result = L.polyline(coordinates, {
          ...defaultStyle,
          objId: route.routeId, // добавляем данные в options
          name: route.routeName,
          address: route.address
        })

        break
      case 'polygon':
        result = L.polygon(coordinates, {
          ...defaultStyle,
          objId: route.routeId, // добавляем данные в options
          name: route.routeName,
          address: route.address
        })
        break

      default:
        break
    }

    return result
  }

  createShapeStyles(shapeColor) {
    let shapeStyles

    const defaultStyle = {
      color: shapeColor || MapConstans.DEFAULT_SHAPE_COLOR,
      weight: 5,
      opacity: 0.7,
      dashArray: '10, 10'
    }

    const highlightStyle = {
      color: MapConstans.HIGHLIGHT_SHAPE_COLOR,
      weight: 6,
      opacity: 1,
      dashArray: null
    }

    shapeStyles = {
      defaultStyle,
      highlightStyle
    }

    return shapeStyles
  }

  groupeRoutesByCity() {
    const routes = this.routes
    const groupedRoutes = {}

    for (const [routeId, route] of Object.entries(routes)) {
      const routeData = route.routedata
      const city = routeData.routeCity || 'Неизвестный город'
      if (!groupedRoutes[city]) {
        groupedRoutes[city] = []
      }
      groupedRoutes[city].push({ routeId, route })
    }

    return groupedRoutes
  }

  selectRoute(routeId) {
    const route = this.routes[routeId]

    if (route) {
      this.deselectRoute()

      route.isSelected = true
      this.currentRoute = route

      route.shape.setStyle(this.shapeStyles.highlightStyle)
    }
  }

  deselectRoute() {
    if (this.currentRoute) {
      this.resetRoute(this.currentRoute)
    }
  }

  resetRoute(route) {
    this.currentRoute = null

    route.isSelected = false
    route.shape.setStyle(route.shapeStyles.defaultStyle)
    route.shape.closePopup()
  }
}

class MarkerService {
  routeMarkers = {}
  prevMarker
  currentMarker
  nextMarker
  markerStyles

  constructor() {
    this.setMarkerStyles()
  }

  createRouteMarkers(route) {
    const self = this
    let markers = []
    let markerData = {}
    const markerStyles = this.markerStyles

    // Добавляем маркеры для каждой точки
    route.points.forEach((point, index) => {
      markerData = {
        name: point.name,
        id: point.id,
        address: point.address,
        key: point.key,
        description: point.description,
        photoPaths: point.photoPaths,
        photoDescriptions: point.photoDescriptions,
        role: point.role,
        routeId: route.routeId
      }

      const markerIcon = self.getMarkerIcon(markerData)

      const marker = L.marker(point.coordinates, {
        routeId: route.routeId,
        index: index,
        activeIcon: MapConstans.DEFAULT_ACTIVE_MARKER_MENU_ICON,
        icon: markerIcon,
        markerData,
        markerStyles,
        isSelected: false
      })

      markers.push(marker)
    })
    this.routeMarkers[route.routeId] = markers
  }

  init(route) {
    console.log('Иницализация MarkerService')
    console.log('route', route)

    this.markerStyles = route.markerStyles

    this.createRouteMarkers(route.routedata)
  }

  selectMarker(marker) {
    const markers = this.routeMarkers[marker.options.routeId]
    const startEndMarker =
      markers.at(-1)?.options?.markerData?.role === 'start-end'
        ? markers.at(-1)
        : null

    if (this.currentMarker !== marker && this.currentMarker != null) {
      this.resetMarker(this.currentMarker)
    }

    this.setPrevAndNextMarker(marker)

    this.currentMarker = marker
    const markerStyles = this.markerStyles
    marker.options.isSelected = true

    if (marker.options.markerData.role === 'between') {
      marker.setIcon(markerStyles.highlightDefaultMarker)
    } else if (marker.options.markerData.role === 'between-multimedia') {
      marker.setIcon(markerStyles.highlightDefaultMultimediaMarker)
    } else {
      //Если маркер начала маршрута, так же активируем маркер конца
      if (marker.options.index === 0 && startEndMarker != null) {
        startEndMarker.setIcon(markerStyles.highlightMarker)
      }
      marker.setIcon(markerStyles.highlightMarker)
    }
  }

  setPrevAndNextMarker(marker) {
    const markerIndex = marker.options.index
    const markers = this.routeMarkers[marker.options.routeId]
    const markerData = marker.options.markerData

    switch (markerData.role) {
      case 'start':
        this.prevMarker = null
        this.nextMarker = markers[markerIndex + 1]
        break
      case 'end':
        this.prevMarker = markers[markerIndex - 1]
        this.nextMarker = null
        break
      case 'start-end':
        if (markerIndex === markers.length - 1) {
          this.prevMarker = markers[markerIndex - 1]
          this.nextMarker = markers[0]
        } else {
          this.prevMarker = markers[markers.length - 1]
          this.nextMarker = markers[markerIndex + 1]
        }
        break
      default:
        this.prevMarker = markers[markerIndex - 1]
        this.nextMarker = markers[markerIndex + 1]
        break
    }
  }

  deselectMarker() {
    if (this.currentMarker) {
      this.resetMarker(this.currentMarker)
    }
  }

  resetMarker(marker) {
    this.currentMarker = null
    const markers = this.routeMarkers[marker.options.routeId]
    const startEndMarker =
      markers.at(-1)?.options?.markerData?.role === 'start-end'
        ? markers.at(-1)
        : null
    marker.options.isSelected = false

    this.setMarkerIcon(marker)

    if (marker.options.index === 0 && startEndMarker != null) {
      this.setMarkerIcon(startEndMarker)
    }

    marker.closePopup()
  }

  setMarkerIcon(marker) {
    const markerData = marker.options.markerData
    const markerStyles = this.markerStyles

    switch (markerData.role) {
      case 'start':
        marker.setIcon(markerStyles.startMarker)
        break
      case 'end':
        marker.setIcon(markerStyles.endMarker)
        break
      case 'start-end':
        marker.setIcon(markerStyles.startEndMarker)
        break
      case 'point':
        marker.setIcon(markerStyles.pointMarker)
        break
      case 'between-multimedia':
        marker.setIcon(markerStyles.defaultMiltimediaMarker)
        break
      case 'between':
        marker.setIcon(markerStyles.defaultMarker)
        break
      default:
        break
    }
  }

  getMarkerActiveIconClassName(marker) {
    return marker.options.markerData.activeIcon
  }

  setCurrentMarkerActiveIcon(iconClassName) {
    marker.options.markerData.activeIcon = iconClassName
  }

  isMarkerDefault(markerData) {
    const defaultMarkerName = 'Промежуточная точка'

    if (markerData.name === defaultMarkerName) {
      return true
    }
    return false
  }

  setMarkerStyles() {
    const markerIconClass = MapConstans.MARKER_POINTS_ICON_CLASSLIST
    const markerDefaultMultimediaIconClass =
      MapConstans.MARKER_BETWEEN_MULTIMEDIA_POINTS_ICON_CLASSLIST
    const markerDefaultIconClass =
      MapConstans.MARKER_BETWEEN_POINTS_ICON_CLASSLIST
    const iconAnchor = [6, 26]
    const defaultMultimediaIconAnchor = [6, 20]
    const defaultconAnchor = [10, 23]
    const popupAnchor = [0, -200]

    const startMarker = L.divIcon({
      description: 'Маркер точки начала маршрута',
      className: 'default-boxicon-marker',
      html: `<i class='${markerIconClass} map-start-route-marker'></i>`,
      iconAnchor: iconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const endMarker = L.divIcon({
      description: 'Маркер точки конца маршрута',
      className: 'default-boxicon-marker',
      html: `<i class='${markerIconClass} map-end-route-marker'></i>`,
      iconAnchor: iconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const startEndMarker = L.divIcon({
      description: 'Маркер точки начала и конца маршрута',
      className: 'default-boxicon-marker',
      html: `<span class="icon-layer"><i class="${markerIconClass} map-end-route-marker base"></i><i class="${markerIconClass} map-end-route-markers top"></i></span>`,
      iconAnchor: iconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const pointMarker = L.divIcon({
      description: 'Маркер точки маршрута',
      className: 'default-boxicon-marker',
      html: `<i class='${markerIconClass} map-point-marker'></i>`,
      iconAnchor: iconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const highlightMarker = L.divIcon({
      description: 'Маркер выбранной точки маршрута',
      className: 'highlight-boxicon-marker',
      html: `<i class='${markerIconClass} map-highlight-marker'></i>`,
      iconAnchor: iconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const defaultMiltimediaMarker = L.divIcon({
      description: 'Маркер промежуточной точки маршрута с мультимедиа файлами',
      className: 'default-boxicon-marker',
      html: `<i class='${markerDefaultMultimediaIconClass} map-default-multimedia-marker'></i>`,
      iconAnchor: defaultMultimediaIconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const highlightDefaultMultimediaMarker = L.divIcon({
      description: 'Маркер промежуточной точки маршрута с мультимедиа файлами',
      className: 'highlight-boxicon-marker',
      html: `<i class='${markerDefaultMultimediaIconClass} map-highlight-default-multimedia-marker'></i>`,
      iconAnchor: defaultMultimediaIconAnchor,
      popupAnchor: popupAnchor
    })

    const defaultMarker = L.divIcon({
      description: 'Маркер промежуточной точки маршрута',
      className: 'default-boxicon-marker',
      html: `<i class='${markerDefaultIconClass} map-default-marker'></i>`,
      iconAnchor: defaultconAnchor, // точка привязки (центр нижней части иконки)
      popupAnchor: popupAnchor
    })

    const highlightDefaultMarker = L.divIcon({
      description: 'Маркер выбранной промежуточной точки маршрута',
      className: 'highlight-boxicon-marker',
      html: `<i class='${markerDefaultIconClass} map-highlight-default-marker'></i>`,
      iconAnchor: defaultconAnchor,
      popupAnchor: popupAnchor
    })

    this.markerStyles = {
      startMarker,
      endMarker,
      startEndMarker,
      pointMarker,
      highlightMarker,
      defaultMiltimediaMarker,
      highlightDefaultMultimediaMarker,
      defaultMarker,
      highlightDefaultMarker
    }
  }

  getMarkerIcon(markerData) {
    let result = null
    const markerStyles = this.markerStyles
    const startMarker = markerStyles.startMarker
    const endMarker = markerStyles.endMarker
    const startEndMarker = markerStyles.startEndMarker
    const pointMarker = markerStyles.pointMarker
    const defaultMarker = markerStyles.defaultMarker
    const defaultMultimediaMarker = markerStyles.defaultMiltimediaMarker

    switch (markerData.role) {
      case 'start':
        result = startMarker
        break
      case 'end':
        result = endMarker
        break
      case 'start-end':
        result = startEndMarker
        break
      case 'point':
        result = pointMarker
        break
      case 'between-multimedia':
        result = defaultMultimediaMarker
        break
      case 'between':
        result = defaultMarker
        break
      default:
        break
    }
    return result
  }

  isMarkerDefault(markerData) {
    const defaultMarkerName = 'Промежуточная точка'

    if (markerData.name === defaultMarkerName) {
      return true
    }
    return false
  }
}

export { handleMapNavigation }
