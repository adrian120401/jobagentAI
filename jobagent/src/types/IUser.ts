export interface IUser {
    id: string;
    name: string;
    email: string;
    cv_path: string;
    docx_path: string;
}

export interface LoginResponse {
    user: IUser;
    token: string;
}

export interface IUserFile {
    url: string;
}