import {encode} from "js-base64";

export const styleUrl =
    (content: string) =>
        dataUrl('text/css', content);

export const scriptUrl =
    (content: string) =>
        dataUrl('application/javascript', content);



export const dataUrl = (type: string, content: string) => {
  return `data:${type};base64,${encode(content)}`
}
