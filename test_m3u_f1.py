import urllib.request
import ssl
import re

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

urls_to_search = [
    "https://iptv-org.github.io/iptv/categories/auto.m3u",
    "https://iptv-org.github.io/iptv/categories/sports.m3u",
    "https://raw.githubusercontent.com/LuanSilvav/Canais-IPTV/main/Canais.m3u"
]

found = []
for m3u_url in urls_to_search:
    try:
        req = urllib.request.Request(m3u_url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, context=ctx, timeout=8) as r:
            lines = r.read().decode('utf-8', errors='ignore').splitlines()
            current_name = ""
            current_logo = ""
            for line in lines:
                if line.startswith("#EXTINF"):
                    m_name = re.search(r',([^,]+)$', line)
                    current_name = m_name.group(1).strip() if m_name else "Unknown"
                    m_logo = re.search(r'tvg-logo="([^"]+)"', line)
                    current_logo = m_logo.group(1).strip() if m_logo else ""
                elif line.startswith("http") and current_name:
                    if any(k in current_name.lower() or k in line.lower() for k in ['f1', 'formula', 'motorsport', 'racing', 'bandsports', 'motor']):
                        found.append((current_name, line.strip(), current_logo))
                    current_name = ""
    except Exception as e:
        print(f"Error fetching {m3u_url}: {e}")

print(f"Total F1/Auto candidates found: {len(found)}")
# Test top candidates
valid = []
for name, link, logo in found[:25]:
    try:
        t_req = urllib.request.Request(link, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(t_req, context=ctx, timeout=3) as tr:
            body = tr.read(150).decode('utf-8', errors='ignore')
            if '#EXTM3U' in body or tr.status == 200:
                print(f"VALID: {name} -> {link}")
                valid.append((name, link, logo))
    except Exception:
        pass

print(f"Total valid: {len(valid)}")
