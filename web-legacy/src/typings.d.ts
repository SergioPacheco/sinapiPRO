declare class ApexCharts {
  constructor(el: any, options: any);
  render(): Promise<void>;
  updateOptions(options: any): Promise<void>;
  destroy(): void;
}
