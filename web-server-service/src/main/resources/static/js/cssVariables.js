/* istanbul ignore file */
// Получаем значение CSS глобальных перменных
const rootStyles = getComputedStyle(document.documentElement);
export const pageTextColorHover = rootStyles
  .getPropertyValue("--page-text-color-hover")
  .trim();
export const sidebarMenuBackgroundColorHover = rootStyles
  .getPropertyValue("--cabinet-page-menu-item-hover-background-color")
  .trim();
export const alVadudTextColor = rootStyles
  .getPropertyValue("--al-vadud-text-color")
  .trim();
