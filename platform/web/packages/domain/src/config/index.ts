export interface PlatformConfig {
    url: string
}
export interface AdminConfig {
    url: string
}

export interface Config {
    platform: PlatformConfig
    admin: AdminConfig
    theme?: ThemeConfig
}

export interface ThemeConfig {
    colors?: ThemeColorsConfig,
    shadow?: string
    font?: string
    rotation?: string
    numberFont?: string
    borderRadius?: string
    logo?: {
        url?: string
    }
}

export interface ThemeColorsConfig {
    primary?: string,
    secondary?: string,
    background?: string
    local?: Record<string, string>
}

// @ts-ignore
export const config: () => Config = () => window._env_.config
