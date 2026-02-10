export default function Footer() {
  return (
    <footer className="bg-spotify-black border-t border-spotify-border py-8 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* About */}
          <div>
            <h3 className="text-white font-bold mb-4">MUTI</h3>
            <p className="text-spotify-gray-light text-sm">
              음악을 통해 사람들을 연결하는 플랫폼
            </p>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-white font-bold mb-4">Quick Links</h3>
            <ul className="space-y-2">
              <li>
                <a href="#" className="text-spotify-gray-light hover:text-white text-sm transition-colors">
                  About
                </a>
              </li>
              <li>
                <a href="#" className="text-spotify-gray-light hover:text-white text-sm transition-colors">
                  Community
                </a>
              </li>
              <li>
                <a href="#" className="text-spotify-gray-light hover:text-white text-sm transition-colors">
                  Support
                </a>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-white font-bold mb-4">Contact</h3>
            <p className="text-spotify-gray-light text-sm">
              GitHub: <a href="https://github.com" className="hover:text-white transition-colors">@muti</a>
            </p>
          </div>
        </div>

        <div className="mt-8 pt-8 border-t border-spotify-border">
          <p className="text-center text-spotify-gray-light text-sm">
            &copy; {new Date().getFullYear()} MUTI. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}