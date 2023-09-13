export class SpoofButton {
  public name: string;
  public text: string;
  public unit: string;
  public url: string;
  public catVisible: boolean;

  public static createInstance(name, text, unit, url, catVisible): SpoofButton {
    return new SpoofButton(name, text, unit, url, catVisible);
  }

  constructor(name, text, unit, url, catVisible) {
    this.name = name;
    this.text = text;
    this.unit = unit;
    this.url = url;
    this.catVisible = catVisible;

  }
}
