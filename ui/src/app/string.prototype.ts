interface String {
  replaceAll(search: string, replacement: string): string;
}

if (!String.prototype.hasOwnProperty('replaceAll')) {
  String.prototype.replaceAll = function(search: string, replacement: string): string {
    return this.replace(new RegExp(search, 'g'), replacement);
  }
}
