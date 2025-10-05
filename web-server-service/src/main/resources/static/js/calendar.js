import { Calendar } from '@fullcalendar/core'
import multiMonthPlugin from '@fullcalendar/multimonth'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import bootstrap5Plugin from '@fullcalendar/bootstrap5'
import ruLocale from '@fullcalendar/core/locales/ru'

export function calendarInit(calendarEl) {
  const calendar = new Calendar(calendarEl, {
    contentHeight: 260,
    plugins: [
      multiMonthPlugin,
      dayGridPlugin,
      interactionPlugin,
      bootstrap5Plugin
    ],
    initialView: 'dayGridMonth',
    multiMonthMaxColumns: 1,
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'multiMonthYear,dayGridMonth'
    },
    locale: {
      ...ruLocale,
      buttonText: {
        ...ruLocale.buttonText,
        multiMonthYear: 'Год',
        dayGridMonth: 'Месяц'
      }
    },
    selectable: true,
    dateClick: function (date) {
      console.log('Клик по дате: ', date.dateStr)
    },
    select: function (info) {
      console.log('Выбранный период от ' + info.startStr + ' до ' + info.endStr)
    }
  })

  calendar.render()

  return calendar
}
