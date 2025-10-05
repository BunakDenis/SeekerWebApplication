export class Modal {
  modalTemplate = document.getElementById('cnt-item-modal-container')
  cntId = null

  constructor(cntItem) {
    this.cntItem = cntItem
    this.getCntId()
    this.clearModal()
  }

  getCntId(cntItem) {
    if (cntItem != null) {
      if (this.cntItem.tagName == 'TR') {
        cntId = this.cntItem.closest('table').id
      } else {
        cntId = this.cntItem.closest('.sb-cnt-title-container').id
      }
    }
  }

  openModal() {
    this.modalTemplate.classList.add('show')

    this.setModalTitle()
  }

  clearModal() {
    const modal = this.modalTemplate
    const inputFields = modal.querySelectorAll('.input-field')

    for (let i = 0; i < inputFields.length; i++) {
      inputFields[i].remove()
    }
  }

  setModalTitle() {
    const modalTitle = this.modalTemplate.querySelector('.cnt-item-modal-title')
    modalTitle.textContent = 'Модальное окно'
  }

  closeModal() {
    this.modalTemplate.classList.remove('show')
  }
}

export class ModalImage {
  modalTemplate = null
  imagesModalContainer = null
  selectedImageWrapper = null
  compactImagesWrapper = null
  imagesContainers = []
  activeImagesContainerIndex = null

  constructor() {
    this.initModalContainers()

    this.initListeners()
  }

  open(imagesContainers) {
    //Инициализация структуры модального окна
    const activeImageContainerIndex = imagesContainers.findIndex(container =>
      container.classList.contains('active')
    )

    this.activeImagesContainerIndex = activeImageContainerIndex

    const activeImageContainer = imagesContainers[activeImageContainerIndex]
    const photoModal = this.modalTemplate
    const photoModalContainer = this.imagesModalContainer
    const photosCompactWrapper = this.compactImagesWrapper
    const selectedImageContainer = this.selectedImageWrapper

    const selectedImage = activeImageContainer.querySelector('img')

    const selectedImageTitle = activeImageContainer.querySelector('h5')

    if (selectedImage) {
      selectedImageContainer.appendChild(selectedImage.cloneNode(true))
    }

    if (selectedImageTitle) {
      selectedImageContainer.appendChild(selectedImageTitle.cloneNode(true))
    }

    photoModalContainer.appendChild(selectedImageContainer)

    if (imagesContainers.length > 1) {
      photosCompactWrapper.setAttribute(
        'active-image-container-index',
        activeImageContainerIndex
      )

      for (let i = 0; i < imagesContainers.length; i++) {
        const photoCompactContainer = document.createElement('div')
        photoCompactContainer.classList.add('photo-compact-container')

        const elementTitle = imagesContainers[i].querySelector('h5')
        const element = imagesContainers[i].querySelector('img')

        if (i === activeImageContainerIndex) {
          photoCompactContainer.classList.add('active')
        }

        if (elementTitle) {
          photoCompactContainer.appendChild(elementTitle)
        }

        if (element) {
          photoCompactContainer.appendChild(element)
        }

        photosCompactWrapper.appendChild(photoCompactContainer)
      }

      photosCompactWrapper.classList.add('show')
      photoModalContainer.appendChild(photosCompactWrapper)

      this.imagesContainers = Array.from(
        photosCompactWrapper.querySelectorAll('.photo-compact-container')
      )

      selectedImageContainer.classList.add('compact')
    } else {
      this.imagesContainers = Array.from(
        selectedImageContainer.querySelectorAll('.photo-compact-container')
      )
    }

    this.toggleVisibilityShiftPhotoIcons()
    photoModal.appendChild(photoModalContainer)
    photoModal.classList.add('show')
  }

  isModalOpen() {
    return this.modalTemplate.classList.contains('show')
  }

  initModalContainers() {
    const photoModal = document.getElementById('map-photo-modal-wrapper')
    const photoModalContainer = photoModal.querySelector(
      '.map-photos-modal-container'
    )
    const photosCompactWrapper = photoModal.querySelector(
      '.map-photos-compact-wrapper'
    )
    const selectedImageContainer = photoModal.querySelector(
      '.selected-photo-container'
    )

    this.modalTemplate = photoModal
    this.imagesModalContainer = photoModalContainer
    this.selectedImageWrapper = selectedImageContainer
    this.compactImagesWrapper = photosCompactWrapper
  }

  toggleVisibilityShiftPhotoIcons() {
    const photoModal = this.modalTemplate
    const index = this.activeImagesContainerIndex
    const arrayLength = this.imagesContainers.length
    const prevIcon = photoModal.querySelector('.bxs-chevron-left')
    const nextIcon = photoModal.querySelector('.bxs-chevron-right')

    console.log('index: ', index)
    console.log('arrayLength: ', arrayLength)
    console.log('prevIcon до проверки: ', prevIcon)
    console.log('nextIcon до проверки: ', nextIcon)

    if (arrayLength === 0) {
      prevIcon.classList.add('hidden')
      nextIcon.classList.add('hidden')
      return
    }
    if (index === arrayLength - 1) {
      prevIcon.classList.remove('hidden')
      nextIcon.classList.add('hidden')
      return
    }
    if (index === 0) {
      prevIcon.classList.add('hidden')
      nextIcon.classList.remove('hidden')
      return
    }

    prevIcon.classList.remove('hidden')
    nextIcon.classList.remove('hidden')

    console.log('prevIcon после проверки: ', prevIcon)
    console.log('nextIcon после проверки: ', nextIcon)
  }

  getActiveImageContainerIndex() {
    return Number(
      this.compactImagesWrapper.getAttribute('active-image-container-index')
    )
  }

  getSelectedImageContainerIndex(container) {
    return this.imagesContainers.indexOf(container)
  }

  changeActiveImageContainer() {
    const imagesContainers = this.imagesContainers
    const activeImageContainerIndex = this.getActiveImageContainerIndex()
    console.log('activeImageContainerIndex: ', activeImageContainerIndex)
    const selectedContainerIndex = this.activeImagesContainerIndex
    const imageCompactWrapper = this.compactImagesWrapper
    const selectedImageWrapper = this.selectedImageWrapper

    this.clearSelectedMediaContainer()

    const activeImageContainer = imagesContainers[activeImageContainerIndex]
    const selectedImageContainer = imagesContainers[selectedContainerIndex]

    activeImageContainer.classList.remove('active')
    selectedImageContainer.classList.add('active')

    const selectedImage = selectedImageContainer.querySelector('img')
    const selectedImageTitle = selectedImageContainer.querySelector('h5')

    if (selectedImage) {
      selectedImageWrapper.appendChild(selectedImage.cloneNode(true))
    }

    if (selectedImageTitle) {
      selectedImageWrapper.appendChild(selectedImageTitle.cloneNode(true))
    }

    imageCompactWrapper.setAttribute(
      'active-image-container-index',
      selectedContainerIndex
    )
  }

  clearSelectedMediaContainer() {
    const selectedImageContainer = this.selectedImageWrapper

    if (selectedImageContainer != null) selectedImageContainer.innerHTML = ''
  }

  clearModal() {
    this.selectedImageWrapper.innerHTML = ''
    this.compactImagesWrapper.innerHTML = ''
    this.imagesContainers = []
    this.activeImagesContainerIndex = null
  }

  closePhotoModal() {
    //Очищаем основной контейнер от активного изображения
    this.clearModal()

    this.selectedImageWrapper.classList.add('compact')
    this.compactImagesWrapper.classList.remove('show')
    this.modalTemplate.classList.remove('show')
  }

  initListeners() {
    this.modalTemplate.addEventListener('click', e => {
      const target = e.target

      if (target.classList.contains('bxs-chevron-left')) {
        const currentIndex = this.activeImagesContainerIndex
        const selectedIndex = currentIndex - 1

        console.log(`bxs-chevron-left`)
        console.log(`currentIndex: ${currentIndex}`)
        console.log(`selectedIndex: ${selectedIndex}`)

        if (currentIndex != null) {
          this.activeImagesContainerIndex = selectedIndex
          this.changeActiveImageContainer()
          this.toggleVisibilityShiftPhotoIcons()
        }
      }

      if (target.classList.contains('bxs-chevron-right')) {
        const currentIndex = this.activeImagesContainerIndex
        const selectedIndex = currentIndex + 1

        console.log(`bxs-chevron-right`)
        console.log(`currentIndex: ${currentIndex}`)
        console.log(`selectedIndex: ${selectedIndex}`)

        if (currentIndex != null) {
          this.activeImagesContainerIndex = selectedIndex
          this.changeActiveImageContainer()
          this.toggleVisibilityShiftPhotoIcons()
        }
      }

      if (target.classList.contains('popup-multimedia-item')) {
        const compactImageContainer = target.closest('.photo-compact-container')
        const selectedImageContainerIndex = this.getSelectedImageContainerIndex(
          compactImageContainer
        )

        if (selectedImageContainerIndex != null) {
          this.activeImagesContainerIndex = selectedImageContainerIndex
          this.changeActiveImageContainer()
          this.toggleVisibilityShiftPhotoIcons()
        }
      }

      if (target.getAttribute('id') === String('photo-modal-exit-icon')) {
        this.closePhotoModal()
      }
    })
  }
}
