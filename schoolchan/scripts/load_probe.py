"""Simple load probe: measure page response times under N concurrent users."""
import argparse, time, concurrent.futures, statistics
import urllib.request

def fetch(url):
    t = time.perf_counter()
    try:
        with urllib.request.urlopen(url, timeout=10) as r:
            r.read()
        return (time.perf_counter() - t) * 1000, None
    except Exception as e:
        return (time.perf_counter() - t) * 1000, str(e)

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--url", default="http://localhost:8080/")
    ap.add_argument("--users", type=int, default=10)
    ap.add_argument("--requests", type=int, default=50)
    args = ap.parse_args()
    times = []; errors = 0
    with concurrent.futures.ThreadPoolExecutor(max_workers=args.users) as ex:
        futs = [ex.submit(fetch, args.url) for _ in range(args.requests)]
        for f in concurrent.futures.as_completed(futs):
            t, e = f.result()
            times.append(t)
            if e: errors += 1
    print(f"requests={args.requests} users={args.users} errors={errors}")
    print(f"mean={statistics.mean(times):.1f}ms median={statistics.median(times):.1f}ms p95={sorted(times)[int(0.95*len(times))]:.1f}ms max={max(times):.1f}ms")

if __name__ == "__main__":
    main()