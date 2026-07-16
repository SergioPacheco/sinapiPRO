/**
 * WeakMap-based cache for permission checks per project.
 *
 * Why WeakMap instead of Map:
 * - When a project object is no longer referenced elsewhere, GC can collect it
 * - Prevents memory leaks in long-lived SPA sessions (user navigates between many projects)
 * - Map would retain project references indefinitely until explicit deletion
 *
 * Usage:
 *   const cache = new PermissionCache();
 *   cache.set(projectRef, new Set(['READ', 'WRITE']));
 *   cache.has(projectRef, 'READ'); // true
 *   // When projectRef goes out of scope → GC cleans the entry automatically
 */
export class PermissionCache {
  private cache = new WeakMap<object, Set<string>>();

  set(key: object, permissions: Set<string>): void {
    this.cache.set(key, permissions);
  }

  has(key: object, permission: string): boolean {
    return this.cache.get(key)?.has(permission) ?? false;
  }

  get(key: object): Set<string> | undefined {
    return this.cache.get(key);
  }

  /** Check if a key exists in cache (permission set is populated) */
  isCached(key: object): boolean {
    return this.cache.has(key);
  }
}
