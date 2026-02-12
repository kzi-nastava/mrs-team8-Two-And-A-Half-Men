import { TabIconDirective } from './tab-icon.directive';
import { TemplateRef } from '@angular/core';

describe('TabIconDirective', () => {
  it('should create an instance', () => {
    const directive = new TabIconDirective(new TemplateRef());
    expect(directive).toBeTruthy();
  });
});
