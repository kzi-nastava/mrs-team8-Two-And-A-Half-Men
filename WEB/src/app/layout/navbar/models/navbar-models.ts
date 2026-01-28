export interface NavbarButton {
  id: string;
  type: 'text' | 'icon' | 'image' | 'notification';
  label?: string;
  icon?: string; 
  imageUrl?: string;
  onClick?: () => void;
  route?: string;
  notificationCount?: number;
  position?: 'left' | 'right'; 
  class?: string; 
}

export interface NavbarSettings {
  isSvgLogo?: boolean; 
  logoUrl?: string;
  logoText?: string;
  backgroundColor?: string;
  textColor?: string;
  buttons: NavbarButton[];
  logoRoute?: string;
}