#!/usr/bin/env python3
"""
Luna - Firestore Seed Data Upload Script
Uploads 820+ fortune readings to Firestore
"""

import json
import sys
import firebase_admin
from firebase_admin import credentials, firestore

def main():
    print("🔥 Initializing Firebase...")
    cred = credentials.Certificate("service-account-key.json")
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    
    json_file = "firestore_fortunes_seed.json"
    print(f"📖 Reading {json_file}...")
    
    try:
        with open(json_file, "r", encoding="utf-8") as f:
            data = json.load(f)
    except FileNotFoundError:
        print(f"❌ ERROR: {json_file} not found!")
        print("   Make sure the file is in the project root.")
        sys.exit(1)
    
    fortunes = data.get("fortunes", [])
    total = len(fortunes)
    print(f"✅ Found {total} fortune readings.")
    
    print(f"\n🚀 Uploading to Firestore...")
    batch = db.batch()
    count = 0
    success = 0
    errors = 0
    
    for fortune in fortunes:
        try:
            doc_id = fortune["id"]
            doc_ref = db.collection("fortunes").document(doc_id)
            batch.set(doc_ref, fortune)
            count += 1
            
            if count % 400 == 0:
                batch.commit()
                batch = db.batch()
                success += 400
                print(f"   ✅ {success}/{total} uploaded...")
                
        except Exception as e:
            errors += 1
            print(f"   ❌ Error ({fortune.get('id', '?')}): {e}")
    
    remaining = count % 400
    if remaining > 0:
        batch.commit()
        success += remaining
    
    print(f"\n{'='*50}")
    print(f"🎉 UPLOAD COMPLETE!")
    print(f"   ✅ Success: {success}")
    print(f"   ❌ Errors:  {errors}")
    print(f"   📊 Total:   {total}")
    print(f"{'='*50}")
    
    print(f"\n🔍 Verifying...")
    docs = db.collection("fortunes").stream()
    db_count = sum(1 for _ in docs)
    print(f"   Firestore has {db_count} documents.")
    
    if db_count == total:
        print(f"   ✅ All data uploaded successfully!")
    else:
        print(f"   ⚠️ Mismatch! Expected: {total}, Actual: {db_count}")
    
    print(f"\n📊 Type Distribution:")
    type_counts = {}
    for f in fortunes:
        t = f.get("type", "unknown")
        type_counts[t] = type_counts.get(t, 0) + 1
    
    for t, c in sorted(type_counts.items(), key=lambda x: -x[1]):
        print(f"   {t:20s}: {c}")

if __name__ == "__main__":
    main()
