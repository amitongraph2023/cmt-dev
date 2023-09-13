export class AuthenticationToken {
	constructor(
		public username: string,
		public firstName: string,
		public lastName: string,
		public displayName: string,
		public knownAs: string,
		public emailAddress: string,
		public accessToken: string,
		public role: string) { }
}
